package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;

/**
 * ProjectRed-style bus transceiver approximation: a gated one-way relay
 * between two bundled-cable networks.
 *
 * <ul>
 *   <li>back face ({@code FACING.getOpposite()}) = input bus</li>
 *   <li>front face ({@code FACING}) = output bus</li>
 *   <li>the four side faces = analog control: while any side is powered the
 *       input bus mask is forwarded to the output bus; unpowered it injects
 *       nothing, so the two networks stay independent.</li>
 * </ul>
 *
 * Transfer is strictly back-to-front, so a transceiver cannot latch its own
 * input network. It emits no analog redstone signal of its own.
 */
public class BusTransceiverBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public BusTransceiverBlock(Properties properties) {
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
     * Wake the cables on both bus faces: the output cable must re-read our
     * injected mask, and the input cable's own network may want to refresh
     * too (harmless if it doesn't).
     */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random) {
        Direction out = state.getValue(FACING);
        for (Direction d : new Direction[]{out, out.getOpposite()}) {
            BlockPos np = pos.relative(d);
            if (level.getBlockState(np).getBlock() instanceof BundledWireBlock w) {
                level.scheduleTick(np, w, 2);
            }
        }
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
     * Mask this transceiver injects into a bundled cable touching
     * {@code wireFace} (the face of this block the cable sits on). Only the
     * front face receives the (gated) input-bus mask; every other face gets 0.
     * Called by {@link BundledWireBlock#tick} while recomputing a network.
     */
    public int injectedMask(Level level, BlockPos pos, BlockState state, Direction wireFace) {
        Direction out = state.getValue(FACING);
        if (wireFace != out || !powered(level, pos, state)) return 0;
        return BundledWireBlock.maskAt(level, pos.relative(out.getOpposite()));
    }
}
