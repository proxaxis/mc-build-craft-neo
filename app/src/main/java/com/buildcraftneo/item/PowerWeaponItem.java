package com.buildcraftneo.item;

import java.util.function.BiConsumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Simply Swords-style melee weapon: attack stats via attribute modifiers,
 * plus a unique right-click power with a cooldown (no MP cost).
 */
public class PowerWeaponItem extends Item {
    private final int cooldownTicks;
    private final BiConsumer<ServerPlayer, ServerLevel> power;

    public PowerWeaponItem(Properties properties, int cooldownTicks,
            BiConsumer<ServerPlayer, ServerLevel> power) {
        super(properties);
        this.cooldownTicks = cooldownTicks;
        this.power = power;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp) {
            player.getCooldowns().addCooldown(player.getItemInHand(hand), cooldownTicks);
            power.accept(sp, (ServerLevel) level);
        }
        return InteractionResult.SUCCESS_SERVER;
    }
}
