package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * ProjectRed-style compact logic gate. Output face = {@code facing};
 * inputs are read from the remaining horizontal faces. Right-click cycles
 * the mode: NOT(back), OR(back|left|right), AND(left&right), XOR(left^right).
 */
public class LogicGateBlockEntity extends BlockEntity {
    public static final String[] MODES = {"not", "or", "and", "xor"};

    private int mode;

    public LogicGateBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.LOGIC_GATE, pos, state);
    }

    public int mode() {
        return mode;
    }

    public void cycleMode() {
        mode = (mode + 1) % MODES.length;
        setChanged();
    }

    public String modeName() {
        return MODES[mode];
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LogicGateBlockEntity be) {
        if (level.isClientSide() || level.getGameTime() % 2 != 0) return;
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction back = facing.getOpposite();
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();
        boolean inBack = powered(level, pos, back);
        boolean inLeft = powered(level, pos, left);
        boolean inRight = powered(level, pos, right);
        boolean out = switch (be.mode) {
            case 0 -> !inBack;
            case 1 -> inBack || inLeft || inRight;
            case 2 -> inLeft && inRight;
            default -> inLeft != inRight;
        };
        if (state.getValue(BlockStateProperties.POWERED) != out) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, out),
                    Block.UPDATE_ALL);
        }
    }

    private static boolean powered(Level level, BlockPos pos, Direction inputSide) {
        return level.getSignal(pos.relative(inputSide), inputSide) > 0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("mode", mode);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        mode = input.getIntOr("mode", 0) % MODES.length;
    }
}
