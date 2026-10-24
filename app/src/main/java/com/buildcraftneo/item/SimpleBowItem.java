package com.buildcraftneo.item;

import net.minecraft.network.chat.Component;
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

/**
 * Longbow (Archers RPG analogue): fires a fast, high-damage arrow. Consumes a
 * vanilla arrow from the player's inventory.
 */
public class SimpleBowItem extends Item {
    private final double damage;
    private final int cooldownTicks;

    public SimpleBowItem(Properties properties, double damage, int cooldownTicks) {
        super(properties);
        this.damage = damage;
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        var inv = sp.getInventory();
        boolean consumed = false;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (!s.isEmpty() && s.is(Items.ARROW)) {
                inv.removeItem(i, 1);
                consumed = true;
                break;
            }
        }
        if (!consumed) {
            sp.sendSystemMessage(Component.literal("[BCN] 矢がありません"));
            return InteractionResult.FAIL;
        }
        Vec3 look = player.getLookAngle();
        Arrow arrow = new Arrow(sp.level(), sp, new ItemStack(Items.ARROW), player.getItemInHand(hand));
        arrow.setPos(player.getX() + look.x, player.getEyeY() + look.y, player.getZ() + look.z);
        arrow.shoot(look.x, look.y, look.z, 4.5f, 0.3f);
        arrow.setBaseDamage(damage);
        arrow.pickup = net.minecraft.world.entity.projectile.arrow.AbstractArrow.Pickup.DISALLOWED;
        sp.level().addFreshEntity(arrow);
        player.getCooldowns().addCooldown(player.getItemInHand(hand), cooldownTicks);
        return InteractionResult.SUCCESS_SERVER;
    }
}
