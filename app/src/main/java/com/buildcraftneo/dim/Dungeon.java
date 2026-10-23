package com.buildcraftneo.dim;

import com.buildcraftneo.industry.BCNBlocks;
import com.buildcraftneo.item.BCNItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/**
 * Procedural dungeon room generator (first-step dimension content):
 * a 9x5x9 stone-brick room with a one-block south entrance, a boss
 * altar in the center and a loot chest in the corner.
 */
public final class Dungeon {
    private Dungeon() {}

    public static final int R = 4;   // half-extent
    public static final int H = 5;   // height

    public static void gen(ServerLevel level, BlockPos c) {
        for (int dx = -R; dx <= R; dx++) {
            for (int dy = 0; dy < H; dy++) {
                for (int dz = -R; dz <= R; dz++) {
                    BlockPos p = c.offset(dx, dy, dz);
                    boolean edge = Math.abs(dx) == R || Math.abs(dz) == R;
                    if (dy == 0 || dy == H - 1) {
                        level.setBlock(p, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                    } else if (edge && !(dx == 0 && dz == -R && dy <= 2)) {
                        level.setBlock(p, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                    } else {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        // boss altar at room center, loot chest in the NE corner
        level.setBlock(c.above(1), BCNBlocks.BOSS_ALTAR.defaultBlockState(), 3);
        BlockPos chest = c.offset(R - 1, 1, R - 1);
        level.setBlock(chest, Blocks.CHEST.defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(chest);
        if (be instanceof ChestBlockEntity box) {
            box.setItem(0, new ItemStack(BCNItems.GOLD_COIN, 3));
            box.setItem(1, new ItemStack(BCNItems.IRON_COIN, 8));
        }
    }
}
