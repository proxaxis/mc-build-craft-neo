package com.buildcraftneo.util;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * CutAll / MineAll / DigAll: breaking a block while sneaking vein-mines
 * connected same-type blocks. Overworld only.
 */
public final class VeinMiner {
    private VeinMiner() {}

    public static final int MAX_BLOCKS = 64;

    private static final int[][] NEIGHBORS = buildNeighbors();

    private static int[][] buildNeighbors() {
        int[][] out = new int[26][3];
        int i = 0;
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    out[i++] = new int[]{x, y, z};
                }
        return out;
    }

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, be) -> {
            if (!(level instanceof ServerLevel sl) || !(player instanceof ServerPlayer sp)) return;
            if (!isOverworld(sl) || !sp.isShiftKeyDown()) return;
            ItemStack tool = sp.getMainHandItem();
            if (isVeinBlock(state) && isRightTool(state, tool)) {
                veinMine(sl, pos, sp, tool);
            }
        });
    }

    public static boolean isVeinBlock(BlockState state) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.ORES) || state.is(BlockTags.DIRT);
    }

    public static boolean isRightTool(BlockState state, ItemStack tool) {
        if (state.is(BlockTags.LOGS)) return tool.is(ItemTags.AXES);
        if (state.is(BlockTags.ORES)) return tool.is(ItemTags.PICKAXES);
        if (state.is(BlockTags.DIRT)) return tool.is(ItemTags.SHOVELS);
        return false;
    }

    private static boolean isOverworld(ServerLevel level) {
        return level.dimension() == Level.OVERWORLD;
    }

    /** Vein-mine same-type connected blocks; returns blocks broken. */
    public static int veinMine(ServerLevel level, BlockPos start, ServerPlayer player, ItemStack tool) {
        BlockState origin = level.getBlockState(start);
        Block target = origin.getBlock();
        Set<BlockPos> seen = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        seen.add(start);
        queue.add(start);
        int broken = 0;

        while (!queue.isEmpty() && broken < MAX_BLOCKS && !tool.isEmpty()) {
            BlockPos cur = queue.poll();
            for (int[] d : NEIGHBORS) {
                BlockPos next = cur.offset(d[0], d[1], d[2]);
                if (!seen.add(next)) continue;
                if (level.getBlockState(next).is(target)) {
                    queue.add(next);
                }
            }
            if (cur.equals(start)) continue; // already broken by the player

            BlockState s = level.getBlockState(cur);
            List<ItemStack> drops = Block.getDrops(s, level, cur,
                    level.getBlockEntity(cur), player, tool);
            level.destroyBlock(cur, false);
            for (ItemStack drop : drops) {
                if (!player.getInventory().add(drop)) {
                    Block.popResource(level, cur, drop);
                }
            }
            tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            broken++;
        }
        return broken;
    }
}
