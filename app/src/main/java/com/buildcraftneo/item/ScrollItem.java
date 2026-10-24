package com.buildcraftneo.item;

import com.buildcraftneo.data.Profiles;
import java.util.function.BiConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Single-use spell scroll (Iron's Spells analogue): casts once, then is consumed. */
public class ScrollItem extends Item {
    private final int mpCost;
    private final BiConsumer<ServerPlayer, ServerLevel> effect;

    public ScrollItem(Properties properties, int mpCost,
            BiConsumer<ServerPlayer, ServerLevel> effect) {
        super(properties);
        this.mpCost = mpCost;
        this.effect = effect;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        if (!Profiles.consumeMp(sp, mpCost)) {
            sp.sendSystemMessage(Component.literal(
                    "[BCN] MP が不足しています (" + mpCost + " 必要)"));
            return InteractionResult.FAIL;
        }
        effect.accept(sp, (ServerLevel) level);
        player.getItemInHand(hand).shrink(1);
        return InteractionResult.SUCCESS_SERVER;
    }
}
