package com.buildcraftneo.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Minimal firearm placeholder: fires a fast projectile on right click.
 * Real damage/handling comes in the weapons phase.
 */
public class GunItem extends Item {
    public GunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp) {
            Vec3 look = player.getLookAngle();
            Snowball projectile = new Snowball(sp.level(), sp, new ItemStack(Items.SNOWBALL));
            projectile.setPos(player.getX() + look.x, player.getEyeY() + look.y, player.getZ() + look.z);
            projectile.shoot(look.x, look.y, look.z, 3.0f, 0.0f);
            sp.level().addFreshEntity(projectile);
            player.getCooldowns().addCooldown(player.getItemInHand(hand), 10);
        }
        return InteractionResult.SUCCESS_SERVER;
    }
}
