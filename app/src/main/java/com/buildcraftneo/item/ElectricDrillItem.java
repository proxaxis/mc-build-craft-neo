package com.buildcraftneo.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * IC2-style electric drill approximation: an EU-charged mining tool.
 * Right-click bores a 3x3 hole into the face being looked at (up to
 * {@value #RANGE} blocks away), draining {@value #COST_PER_BLOCK} EU per
 * block broken. Recharge in a bat_box or on a charge_pad.
 */
public class ElectricDrillItem extends Item implements EuChargeable {
    public static final int CAPACITY = 20_000;
    public static final int COST_PER_BLOCK = 50;
    private static final double RANGE = 8.0;

    public ElectricDrillItem(Properties properties) {
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
        if (eu(stack) < COST_PER_BLOCK) {
            sp.sendSystemMessage(Component.literal(
                    "[BCN] electric_drill: EU不足 (" + eu(stack) + "/" + CAPACITY + ")"));
            return InteractionResult.FAIL;
        }
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(RANGE));
        BlockHitResult hit = level.clip(new ClipContext(eye, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK) {
            sp.sendSystemMessage(Component.literal(
                    "[BCN] electric_drill: 着弾なし (EU残 " + eu(stack) + ")"));
            return InteractionResult.FAIL;
        }
        // 3x3 plane on the hit face: the two axes perpendicular to the face normal
        Direction.Axis axis = hit.getDirection().getAxis();
        Direction.Axis u = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        Direction.Axis v = axis == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y;
        int broken = 0;
        for (int du = -1; du <= 1; du++) {
            for (int dv = -1; dv <= 1; dv++) {
                if (!drainEu(stack, COST_PER_BLOCK)) break;
                BlockPos t = hit.getBlockPos().relative(
                        Direction.fromAxisAndDirection(u, Direction.AxisDirection.POSITIVE), du)
                        .relative(Direction.fromAxisAndDirection(v,
                                Direction.AxisDirection.POSITIVE), dv);
                BlockState bs = level.getBlockState(t);
                if (!bs.isAir() && bs.getDestroySpeed(level, t) >= 0
                        && bs.getDestroySpeed(level, t) < 50.0f) {
                    level.destroyBlock(t, true, player);
                    broken++;
                }
            }
        }
        sp.sendSystemMessage(Component.literal(
                "[BCN] electric_drill: " + broken + " ブロック掘削 (EU残 " + eu(stack) + ")"));
        player.getCooldowns().addCooldown(stack, 10);
        return broken > 0 ? InteractionResult.SUCCESS_SERVER : InteractionResult.FAIL;
    }
}
