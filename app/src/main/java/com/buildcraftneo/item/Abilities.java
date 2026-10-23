package com.buildcraftneo.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Ability implementations used by AbilityItem registrations. */
public final class Abilities {
    private Abilities() {}

    public static void warcry(ServerPlayer p, ServerLevel level) {
        for (ServerPlayer ally : level.players()) {
            if (ally.distanceTo(p) <= 10.0) {
                ally.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 200, 1, true, true));
            }
        }
        for (Entity e : monsters(level, p, 10)) {
            if (e instanceof Mob m) {
                m.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0, true, true));
            }
        }
    }

    public static void taunt(ServerPlayer p, ServerLevel level) {
        for (Entity e : monsters(level, p, 14)) {
            if (e instanceof Mob m) {
                m.setTarget(p);
            }
        }
        p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 1, true, true));
    }

    public static void shadowstep(ServerPlayer p, ServerLevel level) {
        Vec3 look = p.getLookAngle();
        Vec3 target = p.position().add(look.x * 6, 0, look.z * 6);
        p.teleportTo(target.x, target.y, target.z);
        p.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, 1, true, true));
        p.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 60, 0, true, true));
    }

    public static void sanctuary(ServerPlayer p, ServerLevel level) {
        for (ServerPlayer ally : level.players()) {
            if (ally.distanceTo(p) <= 10.0) {
                ally.heal(6.0f);
                ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, true, true));
            }
        }
        p.heal(6.0f);
    }

    public static void meteor(ServerPlayer p, ServerLevel level) {
        Vec3 look = p.getLookAngle();
        Vec3 at = p.getEyePosition().add(look.multiply(15, 15, 15));
        level.explode(p, at.x, at.y, at.z, 4.0f, true,
                net.minecraft.world.level.Level.ExplosionInteraction.NONE);
    }

    public static void frostNova(ServerPlayer p, ServerLevel level) {
        for (Entity e : monsters(level, p, 10)) {
            if (e instanceof Mob m) {
                m.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 3, true, true));
                m.hurtServer(level, level.damageSources().magic(), 6.0f);
            }
        }
    }

    public static void rainOfArrows(ServerPlayer p, ServerLevel level) {
        Vec3 look = p.getLookAngle();
        Vec3 center = p.getEyePosition().add(look.multiply(10, 0, 10));
        for (int i = 0; i < 12; i++) {
            Arrow arrow = new Arrow(level, p, new ItemStack(Items.ARROW), p.getMainHandItem());
            double dx = center.x + (level.getRandom().nextDouble() - 0.5) * 6;
            double dz = center.z + (level.getRandom().nextDouble() - 0.5) * 6;
            arrow.setPos(dx, center.y + 15 + level.getRandom().nextDouble() * 4, dz);
            arrow.shoot(0, -1, 0, 2.0f, 0.5f);
            arrow.setBaseDamage(6.0);
            level.addFreshEntity(arrow);
        }
    }

    public static void pierceShot(ServerPlayer p, ServerLevel level) {
        Arrow arrow = new Arrow(level, p, new ItemStack(Items.ARROW), p.getMainHandItem());
        Vec3 look = p.getLookAngle();
        arrow.setPos(p.getX() + look.x, p.getEyeY() + look.y, p.getZ() + look.z);
        arrow.shoot(look.x, look.y, look.z, 4.5f, 0.0f);
        arrow.setBaseDamage(25.0);
        level.addFreshEntity(arrow);
    }

    private static java.util.List<Entity> monsters(ServerLevel level, ServerPlayer p, double range) {
        return level.getEntities(net.minecraft.world.level.entity.EntityTypeTest.forClass(Entity.class),
                AABB.ofSize(p.position(), range * 2, range * 2, range * 2),
                e -> e instanceof Monster || (e instanceof Mob m && m.getTarget() == p));
    }
}
