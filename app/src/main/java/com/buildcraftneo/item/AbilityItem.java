package com.buildcraftneo.item;

import com.buildcraftneo.data.Job;
import com.buildcraftneo.data.Profiles;
import java.util.function.BiConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/** Job-gated MP ability item (class skills like warcry, taunt, meteor). */
public class AbilityItem extends Item {
    private final Job requiredJob;
    private final int mpCost;
    private final int cooldownTicks;
    private final BiConsumer<ServerPlayer, ServerLevel> effect;

    public AbilityItem(Properties properties, Job requiredJob, int mpCost, int cooldownTicks,
            BiConsumer<ServerPlayer, ServerLevel> effect) {
        super(properties);
        this.requiredJob = requiredJob;
        this.mpCost = mpCost;
        this.cooldownTicks = cooldownTicks;
        this.effect = effect;
    }

    public Job requiredJob() {
        return requiredJob;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        if (Profiles.job(sp) != requiredJob) {
            deny(sp, "このアビリティは職業 " + requiredJob.id() + " 専用です");
            return InteractionResult.FAIL;
        }
        if (!Profiles.consumeMp(sp, mpCost)) {
            deny(sp, "MP が不足しています (" + mpCost + " 必要)");
            return InteractionResult.FAIL;
        }
        player.getCooldowns().addCooldown(player.getItemInHand(hand), cooldownTicks);
        effect.accept(sp, (ServerLevel) level);
        return InteractionResult.SUCCESS_SERVER;
    }

    private static void deny(ServerPlayer p, String msg) {
        p.sendSystemMessage(Component.literal("[BCN] " + msg));
    }
}
