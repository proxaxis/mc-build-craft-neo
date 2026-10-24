package com.buildcraftneo.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Firearm: fires high-velocity, non-pickup arrow projectiles. Item durability
 * acts as the magazine; an empty magazine is refilled by consuming an
 * {@code ammo} item from the inventory (reload on next use).
 */
public class GunItem extends Item {
    private final double damage;
    private final int cooldownTicks;
    private final int pellets;

    public GunItem(Properties properties, double damage, int cooldownTicks) {
        this(properties, damage, cooldownTicks, 1);
    }

    public GunItem(Properties properties, double damage, int cooldownTicks, int pellets) {
        super(properties);
        this.damage = damage;
        this.cooldownTicks = cooldownTicks;
        this.pellets = pellets;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            if (tryReload(sp, stack)) {
                sp.sendSystemMessage(Component.literal("[BCN] リロードしました"));
                player.getCooldowns().addCooldown(stack, 20);
                return InteractionResult.SUCCESS_SERVER;
            }
            sp.sendSystemMessage(Component.literal(
                    "[BCN] マガジンが空です (弾薬が必要です)"));
            return InteractionResult.FAIL;
        }
        String rounds = roundsLoaded(stack);
        Vec3 look = player.getLookAngle();
        for (int i = 0; i < pellets; i++) {
            Arrow bullet = new Arrow(sp.level(), sp,
                    new ItemStack(Items.ARROW), stack);
            bullet.setPos(player.getX() + look.x, player.getEyeY() + look.y, player.getZ() + look.z);
            bullet.shoot(look.x, look.y, look.z, 5.0f, pellets > 1 ? 6.0f : 1.0f);
            if (rounds.equals("ap")) {
                // armor-piercing: +50% damage and critical-hit bonus
                bullet.setBaseDamage(damage * 1.5);
                bullet.setCritArrow(true);
            } else {
                bullet.setBaseDamage(damage);
            }
            if (rounds.equals("inc")) {
                // incendiary: flaming bullet ignites the target
                bullet.igniteForSeconds(5.0f);
            }
            bullet.pickup = net.minecraft.world.entity.projectile.arrow.AbstractArrow.Pickup.DISALLOWED;
            sp.level().addFreshEntity(bullet);
        }
        stack.setDamageValue(stack.getDamageValue() + 1);
        player.getCooldowns().addCooldown(stack, cooldownTicks);
        return InteractionResult.SUCCESS_SERVER;
    }

    /** Ammo type loaded into this gun's magazine ("" = standard). */
    private static String roundsLoaded(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? "" : data.copyTag().getStringOr("rounds", "");
    }

    /** Consume one ammo item from the inventory and refill the magazine. */
    private boolean tryReload(ServerPlayer sp, ItemStack stack) {
        var inv = sp.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            String rounds;
            String label;
            if (s.is(BCNItems.AP_AMMO)) { rounds = "ap"; label = "徹甲弾"; }
            else if (s.is(BCNItems.INC_AMMO)) { rounds = "inc"; label = "焼夷弾"; }
            else if (s.is(BCNItems.AMMO)) { rounds = ""; label = "通常弾"; }
            else continue;
            inv.removeItem(i, 1);
            stack.setDamageValue(0);
            String r = rounds;
            CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                if (r.isEmpty()) t.remove("rounds");
                else t.putString("rounds", r);
            });
            sp.sendSystemMessage(Component.literal("[BCN] " + label + "を装填"));
            return true;
        }
        return false;
    }
}
