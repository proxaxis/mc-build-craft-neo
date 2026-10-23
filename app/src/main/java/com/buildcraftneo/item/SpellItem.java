package com.buildcraftneo.item;

import com.buildcraftneo.data.Profiles;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Simple MP-consuming spell item. Cast effects are implemented in subclasses.
 */
public abstract class SpellItem extends Item {
    private final int mpCost;

    protected SpellItem(Properties properties, int mpCost) {
        super(properties);
        this.mpCost = mpCost;
    }

    public int mpCost() {
        return mpCost;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp) {
            if (!Profiles.consumeMp(sp, mpCost)) {
                sp.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "[BCN] MP が不足しています (" + mpCost + " 必要)"));
                return InteractionResult.FAIL;
            }
            cast(sp);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    protected abstract void cast(ServerPlayer caster);
}
