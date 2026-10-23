package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Minecraft Joules — energy pushed from engines into adjacent consumers. */
public final class Mj {
    private Mj() {}

    /** A block entity that accepts MJ. */
    public interface Consumer {
        /** Accept up to amount MJ, returns leftover. */
        long accept(long amount);
        long stored();
        long capacity();
    }

    /** Push available MJ from source into all adjacent consumers. Returns amount moved. */
    public static long pushToNeighbors(Level level, BlockPos pos, long available) {
        long moved = 0;
        for (Direction dir : Direction.values()) {
            if (available - moved <= 0) break;
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (be instanceof Consumer consumer) {
                moved += (available - moved) - consumer.accept(available - moved);
            }
        }
        return moved;
    }
}
