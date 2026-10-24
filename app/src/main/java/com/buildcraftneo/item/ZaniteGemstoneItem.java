package com.buildcraftneo.item;

import com.buildcraftneo.data.Profiles;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/** Aether-style zanite gemstone: consume to restore 20 MP. */
public class ZaniteGemstoneItem extends Item {
    public ZaniteGemstoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer sp) {
            Profiles.addMp(sp, 20);
            if (!sp.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.getCooldowns().addCooldown(stack, 20);
        }
        return InteractionResult.SUCCESS_SERVER;
    }
}
