package com.buildcraftneo.industry;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;

/**
 * ProjectRed-style frame motor (approximation): on a rising redstone edge,
 * moves the frame structure in front of it one block along its facing.
 * All connected {@code frame} blocks move together; any non-frame block
 * touching a frame is carried along (blocks with block entities, fluids and
 * unbreakable blocks are left behind, and the move is cancelled if the
 * destination is obstructed).
 */
public class FrameMotorBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int MAX_MOVE = 64;

    public FrameMotorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING,
                ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
            Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) return;
        boolean powered = wireOrNeighborSignal(level, pos);
        if (powered && !state.getValue(POWERED)) {
            level.scheduleTick(pos, this, 2);
        } else if (!powered && state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, false), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random) {
        if (wireOrNeighborSignal(level, pos)) {
            level.setBlock(pos, state.setValue(POWERED, true), Block.UPDATE_ALL);
            move(level, pos, state.getValue(FACING));
        }
    }

    /**
     * Redstone or adjacent insulated wire (wires withhold direct signals from
     * conductor blocks, so read their power level directly).
     */
    private static boolean wireOrNeighborSignal(Level level, BlockPos pos) {
        if (level.hasNeighborSignal(pos) || level.getDirectSignalTo(pos) > 0) return true;
        for (Direction d : Direction.values()) {
            BlockState ns = level.getBlockState(pos.relative(d));
            if (ns.getBlock() instanceof InsulatedWireBlock
                    && ns.getValue(InsulatedWireBlock.POWER) > 0) return true;
        }
        return false;
    }

    /** Move the connected frame structure one step in {@code dir}. Public for command tests. */
    public static boolean move(Level level, BlockPos motorPos, Direction dir) {
        BlockPos front = motorPos.relative(dir);
        if (!(level.getBlockState(front).getBlock() == BCNBlocks.FRAME)) return false;

        // BFS all connected frames
        Set<BlockPos> frames = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(front);
        while (!queue.isEmpty() && frames.size() < MAX_MOVE) {
            BlockPos p = queue.poll();
            if (!frames.add(p)) continue;
            for (Direction d : Direction.values()) {
                BlockPos np = p.relative(d);
                if (!frames.contains(np)
                        && level.getBlockState(np).getBlock() == BCNBlocks.FRAME) {
                    queue.add(np);
                }
            }
        }
        if (frames.isEmpty()) return false;

        // frames carry every movable non-frame block they touch
        Set<BlockPos> moveSet = new HashSet<>(frames);
        for (BlockPos f : frames) {
            for (Direction d : Direction.values()) {
                BlockPos np = f.relative(d);
                if (np.equals(motorPos) || moveSet.contains(np)) continue;
                BlockState ns = level.getBlockState(np);
                if (ns.isAir() || ns.hasBlockEntity()
                        || ns.getDestroySpeed(level, np) < 0
                        || !ns.getFluidState().isEmpty()) continue;
                moveSet.add(np);
            }
        }

        // every destination must be free (air or part of the moving set)
        for (BlockPos p : moveSet) {
            BlockPos dest = p.relative(dir);
            if (!moveSet.contains(dest) && !level.getBlockState(dest).isAir()) {
                return false;
            }
        }

        java.util.List<BlockPos> ordered = moveSet.stream()
                .sorted(java.util.Comparator.comparingInt(p -> -p.get(dir.getAxis())))
                .toList();
        java.util.Map<BlockPos, BlockState> states = new java.util.HashMap<>();
        for (BlockPos p : moveSet) states.put(p, level.getBlockState(p));
        for (BlockPos p : moveSet) level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        for (BlockPos p : ordered) {
            level.setBlock(p.relative(dir), states.get(p), Block.UPDATE_ALL);
        }
        return true;
    }
}
