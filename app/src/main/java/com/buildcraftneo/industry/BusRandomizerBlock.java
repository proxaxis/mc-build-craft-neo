package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;

/**
 * ProjectRed-style bus randomizer approximation: while any side face is
 * powered, exactly one channel picked at random from the input bus is
 * forwarded to the output bus. A fresh pick happens on every rising edge of
 * the control signal; while unpowered the output is zero.
 *
 * <ul>
 *   <li>back face ({@code FACING.getOpposite()}) = input bus</li>
 *   <li>front face ({@code FACING}) = output bus</li>
 *   <li>the four side faces = analog control input</li>
 * </ul>
 */
public class BusRandomizerBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public BusRandomizerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BusRandomizerBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING,
                ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos,
            BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide() && !oldState.is(this)) {
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
            Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, 2);
        }
    }

    /**
     * Re-evaluate the control input, re-pick on a rising edge, then wake the
     * cables on both bus faces so their networks recompute.
     */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random) {
        if (level.getBlockEntity(pos) instanceof BusRandomizerBlockEntity be) {
            boolean powered = powered(level, pos, state);
            int outMask = 0;
            if (powered) {
                outMask = be.outMask();
                if (!be.prevPowered() || (outMask & inputMask(level, pos, state)) == 0) {
                    outMask = pickRandom(level, inputMask(level, pos, state));
                }
            }
            be.setState(powered, outMask);
        }
        Direction out = state.getValue(FACING);
        for (Direction d : new Direction[]{out, out.getOpposite()}) {
            BlockPos np = pos.relative(d);
            if (level.getBlockState(np).getBlock() instanceof BundledWireBlock w) {
                level.scheduleTick(np, w, 2);
            }
        }
    }

    private int inputMask(Level level, BlockPos pos, BlockState state) {
        return BundledWireBlock.maskAt(level,
                pos.relative(state.getValue(FACING).getOpposite()));
    }

    /** One randomly chosen channel bit out of {@code mask}, or 0 when empty. */
    private int pickRandom(Level level, int mask) {
        mask &= 0xFFFF;
        int bits = Integer.bitCount(mask);
        if (bits == 0) return 0;
        int want = level.getRandom().nextInt(bits);
        for (int ch = 0; ch < 16; ch++) {
            int bit = 1 << ch;
            if ((mask & bit) != 0 && want-- == 0) return bit;
        }
        return 0;
    }

    /** True while any control face receives a redstone signal. */
    private boolean powered(Level level, BlockPos pos, BlockState state) {
        Direction out = state.getValue(FACING);
        Direction in = out.getOpposite();
        for (Direction d : Direction.values()) {
            if (d == out || d == in) continue;
            BlockPos src = pos.relative(d);
            if (Math.max(level.getSignal(src, d), level.getDirectSignal(src, d)) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mask this randomizer injects into a bundled cable touching
     * {@code wireFace} (the face of this block the cable sits on). Only the
     * front face receives the picked channel; every other face gets 0.
     * Called by {@link BundledWireBlock#tick} while recomputing a network.
     */
    public int injectedMask(Level level, BlockPos pos, BlockState state,
            Direction wireFace) {
        if (wireFace != state.getValue(FACING)) return 0;
        return level.getBlockEntity(pos) instanceof BusRandomizerBlockEntity be
                ? be.outMask() : 0;
    }
}
