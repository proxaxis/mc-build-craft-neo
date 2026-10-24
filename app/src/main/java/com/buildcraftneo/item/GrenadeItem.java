package com.buildcraftneo.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Point Blank-style grenade approximation: throws a short-fuse primed TNT
 * ({@value #FUSE_TICKS} ticks) along the player's line of sight. One grenade
 * item is consumed per throw.
 */
public class GrenadeItem extends Item {
    public static final int FUSE_TICKS = 30;

    public GrenadeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        Vec3 look = player.getLookAngle();
        PrimedTnt tnt = new PrimedTnt(level,
                player.getX() + look.x, player.getEyeY() + look.y,
                player.getZ() + look.z, player);
        tnt.setFuse(FUSE_TICKS);
        tnt.setDeltaMovement(look.scale(1.3).add(0, 0.25, 0));
        level.addFreshEntity(tnt);
        if (!player.hasInfiniteMaterials()) stack.shrink(1);
        player.getCooldowns().addCooldown(stack, 20);
        sp.sendSystemMessage(Component.literal("[BCN] grenade: 投擲 (信管 "
                + FUSE_TICKS + "t)"));
        return InteractionResult.SUCCESS_SERVER;
    }
}
