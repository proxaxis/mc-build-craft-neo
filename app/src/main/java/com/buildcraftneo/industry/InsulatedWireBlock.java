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

    /** Wire color: signals only propagate between wires of the same color. */
    private final String color;

    public InsulatedWireBlock(Properties properties) {
        this(properties, "none");
    }

    public InsulatedWireBlock(Properties properties, String color) {
        super(properties);
        this.color = color;
        registerDefaultState(stateDefinition.any().setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
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
        int power = inputPower(level, pos);
        if (power != state.getValue(POWER)) {
            level.setBlock(pos, state.setValue(POWER, power), Block.UPDATE_ALL);
        }
    }

    /** Strongest incoming signal: adjacent source blocks or same-color wire.power - 1. */
    private int inputPower(Level level, BlockPos pos) {
        int power = 0;
        for (Direction dir : Direction.values()) {
            BlockPos nb = pos.relative(dir);
            BlockState ns = level.getBlockState(nb);
            if (ns.getBlock() instanceof InsulatedWireBlock w && w.color.equals(color)) {
                power = Math.max(power, ns.getValue(POWER) - 1);
            } else if (!(ns.getBlock() instanceof InsulatedWireBlock)) {
                power = Math.max(power,
                        Math.max(level.getSignal(nb, dir),
                                level.getDirectSignal(nb, dir)));
            }
        }
        return Math.max(0, power);
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

    /** Gates and direct-signal readers pick the wire's full power level. */
    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos,
            Direction direction) {
        return state.getValue(POWER);
    }
}
