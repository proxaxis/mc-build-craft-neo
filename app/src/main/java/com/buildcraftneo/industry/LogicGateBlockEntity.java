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
    public static final String[] MODES = {"not", "or", "and", "xor", "nand", "nor", "xnor",
            "rs_latch", "buffer", "d_latch"};

    private int mode;
    private boolean latch;
    private boolean prevClock;

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
        boolean out;
        if (be.mode == 7) {
            // RS latch: left = set, right = reset (set wins)
            if (inLeft) be.latch = true;
            else if (inRight) be.latch = false;
            out = be.latch;
        } else if (be.mode == 9) {
            // D latch: back = clock, left = data. Latches data on clock rising edge.
            if (inBack && !be.prevClock) be.latch = inLeft;
            be.prevClock = inBack;
            out = be.latch;
        } else {
            out = switch (be.mode) {
                case 0 -> !inBack;
                case 1 -> inBack || inLeft || inRight;
                case 2 -> inLeft && inRight;
                case 3 -> inLeft != inRight;
                case 4 -> !(inLeft && inRight);
                case 5 -> !(inBack || inLeft || inRight);
                case 8 -> inBack;
                default -> inLeft == inRight;
            };
        }
        if (state.getValue(BlockStateProperties.POWERED) != out) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, out),
                    Block.UPDATE_ALL);
        }
    }

    private static boolean powered(Level level, BlockPos pos, Direction inputSide) {
        // Read both weak and direct signals so insulated_wire (direct-only) can feed gates.
        BlockPos nb = pos.relative(inputSide);
        return level.getSignal(nb, inputSide) > 0
                || level.getDirectSignal(nb, inputSide) > 0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("mode", mode);
        output.putBoolean("latch", latch);
        output.putBoolean("prevClock", prevClock);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        mode = Math.floorMod(input.getIntOr("mode", 0), MODES.length);
        latch = input.getBooleanOr("latch", false);
        prevClock = input.getBooleanOr("prevClock", false);
    }
}
