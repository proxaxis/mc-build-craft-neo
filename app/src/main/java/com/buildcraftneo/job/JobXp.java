package com.buildcraftneo.job;

import com.buildcraftneo.data.Profiles;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;

/**
 * Player Skills-style XP: killing monsters grants XP; bosses grant 100.
 * level = xp / 100 + 1 (see {@link Profiles#level}).
 */
public final class JobXp {
    private JobXp() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (source == null || !(source.getEntity() instanceof ServerPlayer p)) return;
            int gain = entity.entityTags().contains("bcn_boss") ? 100
                    : entity instanceof Monster ? 5 : 1;
            int before = Profiles.level(p);
            Profiles.addXp(p, gain);
            if (Profiles.level(p) > before) {
                p.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "[BCN] Level up! -> " + Profiles.level(p)));
            }
        });
    }
}
