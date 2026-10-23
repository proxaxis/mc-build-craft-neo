package com.buildcraftneo.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Aether-style ambrosium shard: consume to heal a small amount.
 * Drops from {@code buildcraftneo:ambrosium_ore}.
 */
public class AmbrosiumShardItem extends Item {
    public AmbrosiumShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer sp) {
            sp.heal(4.0f);
            if (!sp.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.getCooldowns().addCooldown(stack, 20);
        }
        return InteractionResult.SUCCESS_SERVER;
    }
}
