package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Holds the 16-bit channel mask of a bundled cable node. The mask lives in a
 * block entity (not a blockstate property) because 16 channels need 16 bits -
 * a blockstate property would explode into tens of thousands of variants.
 */
public class BundledWireBlockEntity extends BlockEntity {
    private int mask;

    public BundledWireBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.BUNDLED_WIRE, pos, state);
    }

    public int mask() {
        return mask;
    }

    /** Update the mask; returns true when it changed. */
    public boolean setMask(int m) {
        if (mask == m) return false;
        mask = m & 0xFFFF;
        setChanged();
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("mask", mask);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        mask = in.getIntOr("mask", 0) & 0xFFFF;
    }
}
