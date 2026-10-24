package com.buildcraftneo.job;

import com.buildcraftneo.item.BCNItems;
import com.buildcraftneo.test.TestFakes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * IC2-style powered-armor approximation: the nano/quantum chestplates grant
 * constant protection effects while worn instead of consuming EU charge.
 */
public final class ArmorEffects {
    private ArmorEffects() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) return;
            for (ServerPlayer p : server.getPlayerList().getPlayers()) apply(p);
            for (ServerPlayer p : TestFakes.all()) apply(p);
        });
    }

    public static void apply(ServerPlayer p) {
        var chest = p.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.is(BCNItems.QUANTUM_CHESTPLATE)) {
            grant(p, MobEffects.RESISTANCE, 1);       // Resistance II
            grant(p, MobEffects.FIRE_RESISTANCE, 0); // lava/fire immunity
        } else if (chest.is(BCNItems.NANO_CHESTPLATE)) {
            grant(p, MobEffects.RESISTANCE, 0);       // Resistance I
        }
    }

    private static void grant(ServerPlayer p,
            net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, int amp) {
        if (!p.hasEffect(effect)) {
            p.addEffect(new MobEffectInstance(effect, 60, amp, true, false));
        }
    }
}
