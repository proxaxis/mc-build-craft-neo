package com.buildcraftneo.job;

import com.buildcraftneo.item.BCNItems;
import com.buildcraftneo.test.TestFakes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * IC2-style powered-armor: the nano/quantum suit pieces are
 * {@link com.buildcraftneo.item.EuChargeable} and pay EU for every effect
 * refresh - an uncharged piece grants nothing. Charge them in a bat_box or
 * on a charge_pad.
 * nano: chestplate=Resistance I, helmet=Water Breathing, leggings=Speed I,
 *       boots=Jump Boost I.
 * quantum: chestplate=Resistance II + Fire Resistance, helmet=Night Vision +
 *       Water Breathing, leggings=Speed II, boots=Jump Boost II.
 */
public final class ArmorEffects {
    /** EU drained from a powered-armor piece per 20-tick effect refresh. */
    private static final int EU_PER_GRANT = 40;

    private ArmorEffects() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) return;
            for (ServerPlayer p : server.getPlayerList().getPlayers()) apply(p);
            for (ServerPlayer p : TestFakes.all()) apply(p);
        });
    }

    public static void apply(ServerPlayer p) {
        var head = p.getItemBySlot(EquipmentSlot.HEAD);
        var chest = p.getItemBySlot(EquipmentSlot.CHEST);
        var legs = p.getItemBySlot(EquipmentSlot.LEGS);
        var feet = p.getItemBySlot(EquipmentSlot.FEET);

        if (chest.is(BCNItems.QUANTUM_CHESTPLATE)) {
            grantPowered(p, chest, MobEffects.RESISTANCE, 1);       // Resistance II
            grantPowered(p, chest, MobEffects.FIRE_RESISTANCE, 0); // lava/fire immunity
        } else if (chest.is(BCNItems.NANO_CHESTPLATE)) {
            grantPowered(p, chest, MobEffects.RESISTANCE, 0);       // Resistance I
        }
        if (head.is(BCNItems.QUANTUM_HELMET)) {
            grantPowered(p, head, MobEffects.NIGHT_VISION, 0);
            grantPowered(p, head, MobEffects.WATER_BREATHING, 0);
        } else if (head.is(BCNItems.NANO_HELMET)) {
            grantPowered(p, head, MobEffects.WATER_BREATHING, 0);
        }
        if (legs.is(BCNItems.QUANTUM_LEGGINGS)) {
            grantPowered(p, legs, MobEffects.SPEED, 1);            // Speed II
        } else if (legs.is(BCNItems.NANO_LEGGINGS)) {
            grantPowered(p, legs, MobEffects.SPEED, 0);            // Speed I
        }
        if (feet.is(BCNItems.QUANTUM_BOOTS)) {
            grantPowered(p, feet, MobEffects.JUMP_BOOST, 1);       // Jump Boost II
        } else if (feet.is(BCNItems.NANO_BOOTS)) {
            grantPowered(p, feet, MobEffects.JUMP_BOOST, 0);       // Jump Boost I
        }

        // Point Blank-style ADS approximation: sneaking while holding a
        // scoped gun is an aiming stance - heavy slowdown (narrows the FOV
        // through the movement-speed attribute, the only FOV lever available
        // server-side) plus a slowness penalty like aiming down sights.
        boolean aiming = p.isShiftKeyDown()
                && p.getMainHandItem().getItem()
                        instanceof com.buildcraftneo.item.GunItem
                && com.buildcraftneo.item.GunItem.hasScope(p.getMainHandItem());
        if (aiming) {
            grant(p, MobEffects.SLOWNESS, 1);         // Slowness II = aiming
            p.addTag("bcn_ads");
        } else if (p.entityTags().contains("bcn_ads")) {
            p.removeTag("bcn_ads");
            p.removeEffect(MobEffects.SLOWNESS);
        }
    }

    private static void grant(ServerPlayer p,
            net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, int amp) {
        if (!p.hasEffect(effect)) {
            p.addEffect(new MobEffectInstance(effect, 60, amp, true, false));
        }
    }

    /**
     * Powered-armor grant: refreshes the effect only when the worn piece can
     * pay {@link #EU_PER_GRANT} EU of charge, so a depleted suit grants
     * nothing (IC2-style EU consumption model).
     */
    private static void grantPowered(ServerPlayer p, net.minecraft.world.item.ItemStack armor,
            net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, int amp) {
        if (armor.getItem() instanceof com.buildcraftneo.item.EuChargeable eu
                && eu.drainEu(armor, EU_PER_GRANT)) {
            grant(p, effect, amp);
        }
    }
}
