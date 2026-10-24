package com.buildcraftneo.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * IC2-style chainsaw approximation: right-click a log to fell the whole
 * connected tree (26-neighbour flood fill, max {@value #MAX_LOGS} logs),
 * draining {@value #COST_PER_LOG} EU per log cut.
 */
public class ChainsawItem extends Item implements EuChargeable {
    public static final int CAPACITY = 20_000;
    public static final int COST_PER_LOG = 25;
    public static final int MAX_LOGS = 64;
    private static final double RANGE = 8.0;

    public ChainsawItem(Properties properties) {
        super(properties);
    }

    @Override
    public int euCapacity() {
        return CAPACITY;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (eu(stack) < COST_PER_LOG) {
            sp.sendSystemMessage(Component.literal(
                    "[BCN] chainsaw: EU不足 (" + eu(stack) + "/" + CAPACITY + ")"));
            return InteractionResult.FAIL;
        }
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(RANGE));
        BlockHitResult hit = level.clip(new ClipContext(eye, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK
                || !level.getBlockState(hit.getBlockPos()).is(BlockTags.LOGS)) {
            sp.sendSystemMessage(Component.literal("[BCN] chainsaw: 原木に向けて使用してください"));
            return InteractionResult.FAIL;
        }
        // flood-fill the connected tree
        java.util.Set<BlockPos> seen = new java.util.HashSet<>();
        java.util.ArrayDeque<BlockPos> queue = new java.util.ArrayDeque<>();
        queue.add(hit.getBlockPos());
        int cut = 0;
        while (!queue.isEmpty() && seen.size() < MAX_LOGS) {
            BlockPos p = queue.poll();
            if (!seen.add(p) || !level.getBlockState(p).is(BlockTags.LOGS)) continue;
            if (!drainEu(stack, COST_PER_LOG)) break;
            level.destroyBlock(p, true, player);
            cut++;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos np = p.offset(dx, dy, dz);
                        if (!seen.contains(np)) queue.add(np);
                    }
                }
            }
        }
        sp.sendSystemMessage(Component.literal(
                "[BCN] chainsaw: " + cut + " 原木を伐採 (EU残 " + eu(stack) + ")"));
        player.getCooldowns().addCooldown(stack, 10);
        return cut > 0 ? InteractionResult.SUCCESS_SERVER : InteractionResult.FAIL;
    }
}
