package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;

/**
 * ProjectRed-style bus converter (approximation): bridges an analog redstone
 * signal and the bundled-cable bus.
 *
 * <ul>
 *   <li>{@code FACING} = analog output face: emits a weak+direct signal whose
 *       level equals the index of the highest active channel (0-15).</li>
 *   <li>the opposite face = analog input: an incoming signal of level N
 *       (1-15) drives bundled channel N. Channel 0 (plain "none" wire) has no
 *       analog representation and stays wire-only.</li>
 *   <li>the four remaining side faces connect to {@link BundledWireBlock}
 *       cables.</li>
 * </ul>
 *
 * The conversion is stateless - input and output are recomputed on every read
 * and every scheduled tick - so a converter never latches on its own (a
 * physical redstone loop wired back into the input can still latch, exactly
 * like vanilla dust).
 */
public class BusConverterBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public BusConverterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
     * After any neighbor change: adjacent bundled cables re-read our injected
     * channel, and the analog output face gets refreshed so dust/lamps see
     * the new level.
     */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random) {
        for (Direction d : busFaces(state)) {
            BlockPos np = pos.relative(d);
            if (level.getBlockState(np).getBlock() instanceof BundledWireBlock w) {
                level.scheduleTick(np, w, 2);
            }
        }
        level.updateNeighborsAt(pos.relative(state.getValue(FACING)), this);
    }

    /** The four bundled-cable faces (everything except the analog in/out faces). */
    private static java.util.List<Direction> busFaces(BlockState state) {
        Direction out = state.getValue(FACING);
        Direction in = out.getOpposite();
        java.util.List<Direction> faces = new java.util.ArrayList<>(4);
        for (Direction d : Direction.values()) {
            if (d != out && d != in) faces.add(d);
        }
        return faces;
    }

    /**
     * Channel bit this converter injects into any touching bundled bus,
     * derived from the analog input on the back face: signal N -> channel N.
     * Called by {@link BundledWireBlock#tick} while recomputing a network.
     */
    public int injectedMask(Level level, BlockPos pos, BlockState state) {
        Direction in = state.getValue(FACING).getOpposite();
        BlockPos src = pos.relative(in);
        int s = Math.max(level.getSignal(src, in), level.getDirectSignal(src, in));
        return s > 0 ? 1 << Math.min(s, 15) : 0;
    }

    /** OR of all bundled masks visible on the side faces. */
    private int busMask(BlockGetter level, BlockPos pos, BlockState state) {
        int mask = 0;
        for (Direction d : busFaces(state)) {
            mask |= BundledWireBlock.maskAt(level, pos.relative(d));
        }
        return mask;
    }

    /** Highest active channel index (0-15) = analog output level. */
    private int outputLevel(BlockGetter level, BlockPos pos, BlockState state) {
        int mask = busMask(level, pos, state);
        if (level instanceof Level l) {
            mask |= injectedMask(l, pos, state);
        }
        return mask == 0 ? 0 : Integer.SIZE - 1 - Integer.numberOfLeadingZeros(mask);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    /**
     * Weak power toward the front face so lamps and dust read it. The reader
     * sits at {@code pos.relative(direction.getOpposite())}; emitting on the
     * FACING face means {@code direction == FACING.getOpposite()}.
     */
    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos,
            Direction direction) {
        if (direction != state.getValue(FACING).getOpposite()) return 0;
        return outputLevel(level, pos, state);
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos,
            Direction direction) {
        return getSignal(state, level, pos, direction);
    }
}
