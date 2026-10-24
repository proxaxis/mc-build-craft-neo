package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Holds a {@link BusRandomizerBlock}'s picked output channel mask and the
 * previous control-signal state, so rising edges can be detected and the
 * pick survives a reload while the control stays powered.
 */
public class BusRandomizerBlockEntity extends BlockEntity {
    private int outMask;
    private boolean prevPowered;

    public BusRandomizerBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.BUS_RANDOMIZER, pos, state);
    }

    public int outMask() {
        return outMask;
    }

    public boolean prevPowered() {
        return prevPowered;
    }

    public void setState(boolean powered, int mask) {
        if (prevPowered == powered && outMask == (mask & 0xFFFF)) return;
        prevPowered = powered;
        outMask = mask & 0xFFFF;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("outMask", outMask);
        out.putBoolean("prevPowered", prevPowered);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        outMask = in.getIntOr("outMask", 0) & 0xFFFF;
        prevPowered = in.getBooleanOr("prevPowered", false);
    }
}
