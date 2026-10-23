package com.buildcraftneo.job;

import com.buildcraftneo.BCN;
import com.buildcraftneo.data.BCNAttachments;
import com.buildcraftneo.data.Faction;
import com.buildcraftneo.data.Job;
import com.buildcraftneo.data.Profiles;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/** Periodic job passives: attribute modifiers, effects, MP cap/regen. */
public final class JobEffects {
    private JobEffects() {}

    private static final Identifier SPEED_MOD = BCN.id("job_assassin_speed");
    private static final Identifier ATK_MOD = BCN.id("job_warrior_attack");
    private static final Identifier KB_MOD = BCN.id("job_guardian_kb");

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) return;
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                apply(p, server.getTickCount());
            }
        });
    }

    public static void apply(ServerPlayer p, int tick) {
        Job job = Profiles.job(p);
        Faction faction = Profiles.faction(p);

        attrMul(p, net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED,
                SPEED_MOD, job == Job.ASSASSIN ? 0.30 : null);
        attrMul(p, net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                ATK_MOD, job == Job.WARRIOR ? 0.20 : null);
        attrAdd(p, net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE,
                KB_MOD, job == Job.GUARDIAN ? 1.0 : null);

        // guardian: constant Resistance I (-20% damage taken)
        effect(p, job == Job.GUARDIAN ? MobEffects.RESISTANCE : null, 0);
        // assassin: constant Speed handled by attribute; add Jump Boost for parkour feel
        effect(p, job == Job.ASSASSIN ? MobEffects.JUMP_BOOST : null, 0);

        // healer aura: Regeneration on nearby allies every 5s
        if (job == Job.HEALER && tick % 100 == 0 && p.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            for (ServerPlayer ally : sl.players()) {
                if (ally.distanceTo(p) <= 8.0) {
                    ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, false));
                }
            }
            p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, false));
        }

        // MP cap per job/faction + regen
        int mpMax = job == Job.MAGE ? 200 : (faction == Faction.SCIENCE ? 0 : 100);
        if (Profiles.mpMax(p) != mpMax) {
            p.setAttached(BCNAttachments.MP_MAX, mpMax);
        }
        int regen = job == Job.MAGE ? 3 : (faction == Faction.MAGIC ? 1 : 0);
        if (regen > 0) {
            Profiles.addMp(p, regen);
        }
    }

    private static void attrMul(ServerPlayer p, Holder<Attribute> attr, Identifier id, Double factor) {
        AttributeInstance inst = p.getAttribute(attr);
        if (inst == null) return;
        if (factor == null) {
            inst.removeModifier(id);
        } else {
            inst.addOrUpdateTransientModifier(
                    new AttributeModifier(id, factor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private static void attrAdd(ServerPlayer p, Holder<Attribute> attr, Identifier id, Double add) {
        AttributeInstance inst = p.getAttribute(attr);
        if (inst == null) return;
        if (add == null) {
            inst.removeModifier(id);
        } else {
            inst.addOrUpdateTransientModifier(
                    new AttributeModifier(id, add, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void effect(ServerPlayer p, Holder<net.minecraft.world.effect.MobEffect> effect, int amp) {
        if (effect == null) return;
        if (!p.hasEffect(effect)) {
            p.addEffect(new MobEffectInstance(effect, 60, amp, true, false));
        }
    }}
