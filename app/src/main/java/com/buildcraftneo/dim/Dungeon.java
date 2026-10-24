package com.buildcraftneo.dim;

import com.buildcraftneo.industry.BCNBlocks;
import com.buildcraftneo.item.BCNItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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

    // ---- Twilight Forest-style structures ----

    private static final int MAZE_R = 7; // hedge maze half-extent

    /** Hedge-maze approximation: 15x15 mossy-cobble walls with a central altar. */
    public static void maze(ServerLevel level, BlockPos c) {
        for (int dx = -MAZE_R; dx <= MAZE_R; dx++) {
            for (int dz = -MAZE_R; dz <= MAZE_R; dz++) {
                BlockPos p = c.offset(dx, 0, dz);
                if (mazeWall(dx, dz)) {
                    level.setBlock(p, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3);
                    level.setBlock(p.above(), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3);
                } else {
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(p.above(), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        level.setBlock(c.above(2), BCNBlocks.BOSS_ALTAR.defaultBlockState(), 3);
    }

    private static boolean mazeWall(int dx, int dz) {
        boolean edge = Math.abs(dx) == MAZE_R || Math.abs(dz) == MAZE_R;
        if (edge) {
            // entrances: middle of south and east walls
            return !((dx == 0 && dz == -MAZE_R) || (dz == 0 && dx == MAZE_R));
        }
        if (dx % 2 == 0 && dz % 2 == 0) return true;                  // pillars
        if (dx % 2 == 0) return Math.floorMod(dz + dx / 2, 3) != 0;   // vertical walls w/ gaps
        if (dz % 2 == 0) return Math.floorMod(dx + dz / 2, 3) != 0;   // horizontal walls w/ gaps
        return false;
    }

    /** Lich tower approximation: hollow 5x5 stone-brick tower, 12 high, altar on top. */
    public static void tower(ServerLevel level, BlockPos c) {
        int r = 2, h = 12;
        for (int dy = 0; dy < h; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos p = c.offset(dx, dy, dz);
                    boolean edge = Math.abs(dx) == r || Math.abs(dz) == r;
                    if (dy == 0 || dy == h - 1) {
                        level.setBlock(p, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                    } else if (edge) {
                        // arrow-slit windows every 3 blocks on the walls
                        boolean window = dy % 3 == 1 && (dx == 0 || dz == 0);
                        level.setBlock(p, window ? Blocks.AIR.defaultBlockState()
                                : Blocks.STONE_BRICKS.defaultBlockState(), 3);
                    } else {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        // top chamber: altar + loot chest
        level.setBlock(c.offset(0, h - 1, 0), BCNBlocks.BOSS_ALTAR.defaultBlockState(), 3);
        BlockPos chest = c.offset(1, h - 1, 1);
        level.setBlock(chest, Blocks.CHEST.defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(chest);
        if (be instanceof ChestBlockEntity box) {
            box.setItem(0, new ItemStack(BCNItems.DIAMOND_COIN, 1));
        }
    }

    /** Cataclysm-style boss arena: blackstone ring, lava moat, center altar. */
    public static void arena(ServerLevel level, BlockPos c) {
        int r = 6;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                BlockPos p = c.offset(dx, 0, dz);
                if (dist > r) continue;
                if (dist > r - 1.5) {
                    // outer wall: 3-high blackstone with netherite accents
                    for (int dy = 0; dy < 3; dy++) {
                        Block b = dy == 2 ? Blocks.GILDED_BLACKSTONE : Blocks.BLACKSTONE;
                        level.setBlock(p.above(dy), b.defaultBlockState(), 3);
                    }
                } else if (dist > r - 3.0 && Math.floorMod(dx + dz, 2) == 0) {
                    // lava moat ring
                    level.setBlock(p, Blocks.LAVA.defaultBlockState(), 3);
                    level.setBlock(p.above(), Blocks.AIR.defaultBlockState(), 3);
                } else {
                    level.setBlock(p, Blocks.POLISHED_BLACKSTONE.defaultBlockState(), 3);
                    level.setBlock(p.above(), Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(p.above(2), Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(p.above(3), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        // two side entrances
        for (int dy = 1; dy <= 2; dy++) {
            level.setBlock(c.offset(-r, dy, 0), Blocks.AIR.defaultBlockState(), 3);
            level.setBlock(c.offset(r, dy, 0), Blocks.AIR.defaultBlockState(), 3);
        }
        level.setBlock(c.offset(-r + 1, 0, 0), Blocks.POLISHED_BLACKSTONE.defaultBlockState(), 3);
        level.setBlock(c.offset(r - 1, 0, 0), Blocks.POLISHED_BLACKSTONE.defaultBlockState(), 3);
        level.setBlock(c.above(1), BCNBlocks.BOSS_ALTAR.defaultBlockState(), 3);
    }
}
