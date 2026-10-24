package com.buildcraftneo.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Parachute (Aether analogue): deploying grants slow falling and a small
 * upward boost. Limited number of deployments (durability).
 */
public class ParachuteItem extends Item {

    public ParachuteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            sp.sendSystemMessage(Component.literal("[BCN] パラシュートは破損しています"));
            return InteractionResult.FAIL;
        }
        sp.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 0, true, true));
        sp.push(0, 0.6, 0);
        stack.setDamageValue(stack.getDamageValue() + 1);
        sp.sendSystemMessage(Component.literal("[BCN] パラシュート展開！"));
        return InteractionResult.SUCCESS_SERVER;
    }
}
