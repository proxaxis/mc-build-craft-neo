package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;

/**
 * ProjectRed-style bundled wire: carries up to 16 independent digital
 * channels over one cable. Channels are interfaced through insulated wires
 * of matching color - an uncolored insulated wire reads channel 0, red wire
 * reads channel 1, and so on (see {@link InsulatedWireBlock#bitFor}).
 *
 * The per-node mask is stored in a {@link BundledWireBlockEntity} because a
 * 16-bit mask cannot be a blockstate property. The {@code on} blockstate
 * only drives the lit/unlit texture (mask != 0).
 *
 * Like insulated wires it is a non-conductor and emits only a direct signal
 * toward matching insulated wires, so plain redstone devices next to the
 * cable never see it.
 */
public class BundledWireBlock extends Block implements EntityBlock {
    /** True while the cable carries at least one active channel. */
    public static final BooleanProperty ON = BooleanProperty.create("on");

    public BundledWireBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ON, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ON);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BundledWireBlockEntity(pos, state);
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
     * Recompute the whole connected bus: the mask of every bundled wire in the
     * network is the OR of all external inputs (powered insulated wires that
     * are not bus-driven, plus plain redstone on channel 0). Updating each
     * node to the shared mask settles the network and, unlike pairwise
     * merging, drops channels whose source has gone away - no self-latching.
     */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random) {
        java.util.Set<BlockPos> net = new java.util.HashSet<>();
        java.util.ArrayDeque<BlockPos> queue = new java.util.ArrayDeque<>();
        queue.add(pos);
        while (!queue.isEmpty() && net.size() < 256) {
            BlockPos p = queue.poll();
            if (!net.add(p)) continue;
            for (Direction d : Direction.values()) {
                BlockPos np = p.relative(d);
                if (!net.contains(np)
                        && level.getBlockState(np).getBlock() instanceof BundledWireBlock) {
                    queue.add(np);
                }
            }
        }
        int mask = 0;
        for (BlockPos p : net) mask |= localInput(level, p);
        mask &= 0xFFFF;
        for (BlockPos p : net) {
            if (level.getBlockEntity(p) instanceof BundledWireBlockEntity be) {
                be.setMask(mask);
            }
            BlockState ns = level.getBlockState(p);
            if (ns.getValue(ON) != (mask != 0)) {
                level.setBlock(p, ns.setValue(ON, mask != 0), Block.UPDATE_ALL);
            }
            // wake bus converters so their analog output tracks the new mask
            for (Direction d : Direction.values()) {
                BlockPos np = p.relative(d);
                if (level.getBlockState(np).getBlock() instanceof BusConverterBlock conv) {
                    level.scheduleTick(np, conv, 2);
                }
            }
        }
    }

    /** Current channel mask of a bundled node (0 when it has no block entity). */
    public static int maskAt(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BundledWireBlockEntity be ? be.mask() : 0;
    }

    /** External inputs of a single cable node (insulated wires + plain signals). */
    private int localInput(Level level, BlockPos pos) {
        int mask = 0;
        for (Direction dir : Direction.values()) {
            BlockPos nb = pos.relative(dir);
            BlockState ns = level.getBlockState(nb);
            if (ns.getBlock() instanceof InsulatedWireBlock w) {
                // bus-driven wires are outputs, not inputs (prevents feedback)
                if (ns.getValue(InsulatedWireBlock.POWER) > 0
                        && !ns.getValue(InsulatedWireBlock.BUNDLED)) {
                    mask |= w.channelBit();
                }
            } else if (ns.getBlock() instanceof BundledWireBlock) {
                // merged at network level
            } else if (ns.getBlock() instanceof BusConverterBlock conv) {
                // analog input on the converter's back face drives a channel;
                // its output face must not count as a plain ch0 source
                mask |= conv.injectedMask(level, nb, ns);
            } else {
                int sig = Math.max(level.getSignal(nb, dir), level.getDirectSignal(nb, dir));
                if (sig > 0) mask |= InsulatedWireBlock.bitFor("none");
            }
        }
        return mask;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    /** Bundled cables emit no weak power at all. */
    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos,
            Direction direction) {
        return 0;
    }

    /**
     * Direct signal only towards an insulated wire of matching color.
     * {@code direction} points from the reader to this block, so the reader
     * sits at {@code pos.relative(direction.getOpposite())}.
     */
    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos,
            Direction direction) {
        BlockState reader = level.getBlockState(pos.relative(direction.getOpposite()));
        if (reader.getBlock() instanceof InsulatedWireBlock w) {
            return (maskAt(level, pos) & w.channelBit()) != 0 ? 15 : 0;
        }
        return 0;
    }
}
