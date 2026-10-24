package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;

/**
 * ProjectRed-style insulated wire. Carries redstone along the wire chain
 * (decays by 1 per hop like dust) but emits only a DIRECT signal: logic
 * gates and other components that read {@code getDirectSignal} can use it,
 * while it does not weakly power adjacent lamps, dust or solid blocks.
 * Propagation is event-driven (scheduled ticks on neighbor changes), so a
 * wire network costs no tick time while idle.
 */
public class InsulatedWireBlock extends Block {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    /** True while this wire's strongest input is a bundled cable (bus-driven). */
    public static final BooleanProperty BUNDLED =
            net.minecraft.world.level.block.state.properties.BooleanProperty.create("bundled");

    /**
     * All wire colors in bundled-cable channel order. {@code none} is the
     * plain insulated wire; the other 15 are dyed variants - together they
     * give the full 16-channel bus.
     */
    public static final java.util.List<String> COLORS = java.util.List.of(
            "none", "red", "green", "blue", "white", "orange", "magenta",
            "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
            "cyan", "purple", "brown");

    /** Wire color: signals only propagate between wires of the same color. */
    private final String color;

    public String color() {
        return color;
    }

    /** Bundled-cable channel bit for this wire's color (index into COLORS). */
    public int channelBit() {
        return bitFor(color);
    }

    public static int bitFor(String color) {
        int idx = COLORS.indexOf(color);
        return idx < 0 ? 1 : 1 << idx;
    }

    public InsulatedWireBlock(Properties properties) {
        this(properties, "none");
    }

    public InsulatedWireBlock(Properties properties, String color) {
        super(properties);
        this.color = color;
        registerDefaultState(stateDefinition.any()
                .setValue(POWER, 0).setValue(BUNDLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER, BUNDLED);
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

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random) {
        int[] in = inputPower(level, pos);
        int power = in[0];
        boolean busDriven = in[1] == 1;
        if (power != state.getValue(POWER) || busDriven != state.getValue(BUNDLED)) {
            level.setBlock(pos, state.setValue(POWER, power).setValue(BUNDLED, busDriven),
                    Block.UPDATE_ALL);
        }
    }

    /**
     * Strongest incoming signal: adjacent source blocks, bundled cables or
     * same-color wire.power - 1. Returns {power, busDriven} - the flag marks
     * whether the strongest input came from a bundled cable, so the cable
     * itself never re-reads its own output (avoids bus latching).
     */
    private int[] inputPower(Level level, BlockPos pos) {
        int power = 0;
        boolean bus = false;
        for (Direction dir : Direction.values()) {
            BlockPos nb = pos.relative(dir);
            BlockState ns = level.getBlockState(nb);
            int c;
            boolean fromBus;
            if (ns.getBlock() instanceof InsulatedWireBlock w && w.color.equals(color)) {
                c = ns.getValue(POWER) - 1;
                fromBus = ns.getValue(BUNDLED); // bus-driven flag propagates along the chain
            } else if (ns.getBlock() instanceof InsulatedWireBlock) {
                continue; // different color: fully isolated
            } else if (ns.getBlock() instanceof BundledWireBlock) {
                c = (BundledWireBlock.maskAt(level, nb) & channelBit()) != 0 ? 15 : 0;
                fromBus = true;
            } else {
                // merged signal is safe to read here: this wire emits no direct
                // signal into conductors, so powered blocks can't feed its own
                // output back (which would self-latch the wire)
                c = Math.max(level.getSignal(nb, dir), level.getDirectSignal(nb, dir));
                fromBus = false;
            }
            if (c > power) {
                power = c;
                bus = fromBus;
            } else if (c == power && !fromBus) {
                bus = false; // an equally strong non-bus input wins
            }
        }
        return new int[]{Math.max(0, power), bus ? 1 : 0};
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    /** Insulated: no weak power leaks to neighboring dust/lamps/blocks. */
    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos,
            Direction direction) {
        return 0;
    }

    /**
     * Gates and other non-conductor readers pick the wire's full power level.
     * Solid conductors get nothing: a powered solid block would emit weak
     * power to dust/lamps around it and break the insulation guarantee.
     */
    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos,
            Direction direction) {
        BlockPos readerPos = pos.relative(direction.getOpposite());
        if (level.getBlockState(readerPos).isRedstoneConductor(level, readerPos)) return 0;
        return state.getValue(POWER);
    }
}
