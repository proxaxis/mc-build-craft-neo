package com.buildcraftneo.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Simple firearm: fires a high-velocity, non-pickup arrow projectile. */
public class GunItem extends Item {
    private final double damage;
    private final int cooldownTicks;

    public GunItem(Properties properties, double damage, int cooldownTicks) {
        super(properties);
        this.damage = damage;
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp) {
            var stack = player.getItemInHand(hand);
            if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
                sp.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "[BCN] マガジンが空です (リロードしてください)"));
                return InteractionResult.FAIL;
            }
            Vec3 look = player.getLookAngle();
            Arrow bullet = new Arrow(sp.level(), sp,
                    new ItemStack(Items.ARROW), player.getItemInHand(hand));
            bullet.setPos(player.getX() + look.x, player.getEyeY() + look.y, player.getZ() + look.z);
            bullet.shoot(look.x, look.y, look.z, 5.0f, 1.0f);
            bullet.setBaseDamage(damage);
            bullet.pickup = net.minecraft.world.entity.projectile.arrow.AbstractArrow.Pickup.DISALLOWED;
            sp.level().addFreshEntity(bullet);
            stack.setDamageValue(stack.getDamageValue() + 1);
            player.getCooldowns().addCooldown(player.getItemInHand(hand), cooldownTicks);
        }
        return InteractionResult.SUCCESS_SERVER;
    }
}
