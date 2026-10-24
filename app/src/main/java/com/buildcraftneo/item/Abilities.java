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

    // ---- Iron's Spells-style spells ----

    public static void heal(ServerPlayer p, ServerLevel level) {
        p.heal(12.0f);
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1, true, true));
    }

    public static void lightning(ServerPlayer p, ServerLevel level) {
        Vec3 at = p.pick(20.0, 0.0f, false).getLocation();
        var bolt = new net.minecraft.world.entity.LightningBolt(
                net.minecraft.world.entity.EntityTypes.LIGHTNING_BOLT, level);
        bolt.setPos(at.x, at.y, at.z);
        level.addFreshEntity(bolt);
    }

    public static void blink(ServerPlayer p, ServerLevel level) {
        var hit = p.pick(16.0, 0.0f, false);
        Vec3 target;
        if (hit instanceof net.minecraft.world.phys.BlockHitResult bhr) {
            target = Vec3.atCenterOf(bhr.getBlockPos()).subtract(
                    p.getLookAngle().multiply(1.2, 0, 1.2)).add(0, 0.5, 0);
        } else {
            target = hit.getLocation();
        }
        p.teleportTo(target.x, target.y, target.z);
    }

    public static void holySmite(ServerPlayer p, ServerLevel level) {
        for (Entity e : monsters(level, p, 8)) {
            if (e instanceof Mob m) {
                m.hurtServer(level, level.damageSources().magic(), 14.0f);
                m.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, true, true));
            }
        }
        for (ServerPlayer ally : level.players()) {
            if (ally.distanceTo(p) <= 8.0) ally.heal(4.0f);
        }
    }

    public static void smokeBomb(ServerPlayer p, ServerLevel level) {
        p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 160, 0, true, true));
        p.addEffect(new MobEffectInstance(MobEffects.SPEED, 160, 1, true, true));
        for (Entity e : monsters(level, p, 8)) {
            if (e instanceof Mob m) {
                m.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, true, true));
            }
        }
    }

    public static void fortify(ServerPlayer p, ServerLevel level) {
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 3, true, true));
        p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 1, true, true));
    }

    // ---- Simply Swords-style weapon powers ----

    public static void katanaDash(ServerPlayer p, ServerLevel level) {
        Vec3 look = p.getLookAngle();
        p.teleportTo(p.getX() + look.x * 3, p.getY(), p.getZ() + look.z * 3);
        for (Entity e : monsters(level, p, 4)) {
            ((Mob) e).hurtServer(level, p.damageSources().playerAttack(p), 8.0f);
        }
    }

    public static void shockwave(ServerPlayer p, ServerLevel level) {
        for (Entity e : monsters(level, p, 6)) {
            Mob m = (Mob) e;
            m.hurtServer(level, p.damageSources().playerAttack(p), 12.0f);
            m.push((m.getX() - p.getX()) * 0.9, 0.4, (m.getZ() - p.getZ()) * 0.9);
        }
    }

    public static void lunge(ServerPlayer p, ServerLevel level) {
        Vec3 look = p.getLookAngle();
        p.teleportTo(p.getX() + look.x * 4, p.getY(), p.getZ() + look.z * 4);
    }

    public static void whirlwind(ServerPlayer p, ServerLevel level) {
        for (Entity e : monsters(level, p, 5)) {
            ((Mob) e).hurtServer(level, p.damageSources().playerAttack(p), 9.0f);
        }
    }

    public static void flurry(ServerPlayer p, ServerLevel level) {
        p.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 2, true, true));
        p.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 100, 0, true, true));
    }

    public static void slam(ServerPlayer p, ServerLevel level) {
        for (Entity e : monsters(level, p, 7)) {
            Mob m = (Mob) e;
            m.hurtServer(level, p.damageSources().playerAttack(p), 10.0f);
            m.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, true, true));
        }
    }

    public static void venom(ServerPlayer p, ServerLevel level) {
        for (Entity e : monsters(level, p, 4)) {
            ((Mob) e).addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1, true, true));
        }
    }

    public static void harvest(ServerPlayer p, ServerLevel level) {
        int hits = 0;
        for (Entity e : monsters(level, p, 5)) {
            ((Mob) e).hurtServer(level, p.damageSources().playerAttack(p), 8.0f);
            hits++;
        }
        if (hits > 0) p.heal(hits);
    }

    // ---- RPG series (Paladins / Archers) ----

    /** Paladin: barrier aura - absorption + resistance for nearby allies. */
    public static void barrier(ServerPlayer p, ServerLevel level) {
        for (ServerPlayer ally : level.players()) {
            if (ally.distanceTo(p) <= 8.0) {
                ally.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 1, true, true));
                ally.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 0, true, true));
            }
        }
    }

    /** Priest: consecrate - heals allies and burns undead in a radius. */
    public static void consecrate(ServerPlayer p, ServerLevel level) {
        for (ServerPlayer ally : level.players()) {
            if (ally.distanceTo(p) <= 10.0) ally.heal(8.0f);
        }
        for (Entity e : monsters(level, p, 10)) {
            if (e instanceof Mob m && (e instanceof net.minecraft.world.entity.monster.zombie.Zombie
                    || e instanceof net.minecraft.world.entity.monster.skeleton.AbstractSkeleton
                    || e instanceof net.minecraft.world.entity.monster.Phantom)) {
                m.hurtServer(level, level.damageSources().magic(), 16.0f);
                m.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160, 0, true, true));
            }
        }
    }

    /** Marksman: volley - a forward cone of 5 fast arrows. */
    public static void volley(ServerPlayer p, ServerLevel level) {
        Vec3 look = p.getLookAngle();
        for (int i = 0; i < 5; i++) {
            Arrow arrow = new Arrow(level, p, new ItemStack(Items.ARROW), p.getMainHandItem());
            arrow.setPos(p.getX() + look.x, p.getEyeY() + look.y, p.getZ() + look.z);
            arrow.shoot(look.x, look.y, look.z, 4.0f, 3.0f);
            arrow.setBaseDamage(10.0);
            level.addFreshEntity(arrow);
        }
    }

    private static java.util.List<Entity> monsters(ServerLevel level, ServerPlayer p, double range) {
        return level.getEntities(net.minecraft.world.level.entity.EntityTypeTest.forClass(Entity.class),
                AABB.ofSize(p.position(), range * 2, range * 2, range * 2),
                e -> e instanceof Monster || (e instanceof Mob m && m.getTarget() == p));
    }
}
