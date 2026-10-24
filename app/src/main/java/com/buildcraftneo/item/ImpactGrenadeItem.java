package com.buildcraftneo.item;

import com.buildcraftneo.entity.BCNEntities;
import com.buildcraftneo.entity.GrenadeEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Point Blank-style impact grenade: throws a projectile that detonates on
 * contact (block or entity), unlike the timed {@link GrenadeItem}.
 */
public class ImpactGrenadeItem extends Item {
    public ImpactGrenadeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        GrenadeEntity grenade = new GrenadeEntity(BCNEntities.GRENADE, level);
        grenade.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        grenade.setOwner(player);
        grenade.shootFromRotation(player, player.getXRot(), player.getYRot(),
                0.0f, 1.3f, 1.0f);
        level.addFreshEntity(grenade);
        if (!player.hasInfiniteMaterials()) stack.shrink(1);
        player.getCooldowns().addCooldown(stack, 20);
        sp.sendSystemMessage(Component.literal("[BCN] impact_grenade: 投擲 (着発)"));
        return InteractionResult.SUCCESS_SERVER;
    }
}
