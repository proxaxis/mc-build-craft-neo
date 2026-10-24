package com.buildcraftneo.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * IC2-style mining laser approximation: a rechargeable EU tool. Each shot
 * drains {@value #SHOT_COST} EU and fires a hit-scan beam that melts the
 * first block it hits (up to 24 blocks away). Recharge by leaving it inside
 * a {@code bat_box} machine's charge slot.
 *
 * Charge is stored in {@code CUSTOM_DATA.eu}; new lasers start empty.
 */
public class MiningLaserItem extends Item implements EuChargeable {
    public static final int CAPACITY = 10_000;
    public static final int SHOT_COST = 500;
    private static final double RANGE = 24.0;

    public MiningLaserItem(Properties properties) {
        super(properties);
    }

    @Override
    public int euCapacity() {
        return CAPACITY;
    }

    /** EU of a bare stack (call sites that only hold an ItemStack). */
    public static int euOf(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? 0 : data.copyTag().getIntOr("eu", 0);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (!drainEu(stack, SHOT_COST)) {
            sp.sendSystemMessage(Component.literal(
                    "[BCN] mining_laser: EU不足 (" + eu(stack) + "/" + CAPACITY
                            + ") - bat_boxで充電してください"));
            return InteractionResult.FAIL;
        }
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(RANGE));
        BlockHitResult hit = level.clip(new ClipContext(eye, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos target = hit.getBlockPos();
            if (!level.getBlockState(target).isAir()
                    && level.getBlockState(target).getDestroySpeed(level, target) >= 0
                    && level instanceof ServerLevel sl) {
                level.destroyBlock(target, true, player);
                sp.sendSystemMessage(Component.literal(
                        "[BCN] mining_laser: " + target.toShortString() + " を溶断 (EU残 "
                                + eu(stack) + ")"));
            }
        } else {
            sp.sendSystemMessage(Component.literal(
                    "[BCN] mining_laser: 着弾なし (EU残 " + eu(stack) + ")"));
        }
        player.getCooldowns().addCooldown(stack, 10);
        return InteractionResult.SUCCESS_SERVER;
    }
}
