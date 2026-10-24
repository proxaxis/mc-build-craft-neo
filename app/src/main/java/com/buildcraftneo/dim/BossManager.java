package com.buildcraftneo.dim;

import com.buildcraftneo.BCN;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Tracks spawned dungeon bosses: drives a custom boss bar, Cataclysm-style
 * attack phases (periodic AoE smash, minion summons at HP thresholds),
 * twilight altar progression unlocks, and bonus loot on death.
 */
public final class BossManager {
    private BossManager() {}

    private static final class Tracked {
        final LivingEntity boss;
        final CustomBossEvent bar;
        final BlockPos altarPos;   // null = not linked to an altar
        final int bossIndex;       // twilight altar selection index
        int phaseFlags;            // bit0 = <50% minions spawned, bit1 = <25% burst done

        Tracked(LivingEntity boss, CustomBossEvent bar, BlockPos altarPos, int bossIndex) {
            this.boss = boss;
            this.bar = bar;
            this.altarPos = altarPos;
            this.bossIndex = bossIndex;
        }
    }

    private static final Map<java.util.UUID, Tracked> BOSSES = new ConcurrentHashMap<>();

    public static void track(LivingEntity boss, MinecraftServer server) {
        track(boss, server, null, -1);
    }

    public static void track(LivingEntity boss, MinecraftServer server,
            BlockPos altarPos, int bossIndex) {
        CustomBossEvent bar = server.getCustomBossEvents().create(
                server.overworld().getRandom(),
                BCN.id("boss_" + boss.getUUID().toString().replace("-", "").substring(0, 12)),
                Component.literal(boss.getName().getString()));
        bar.setColor(BossEvent.BossBarColor.RED);
        bar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_10);
        BOSSES.put(boss.getUUID(), new Tracked(boss, bar, altarPos, bossIndex));
    }

    public static boolean bossAliveAt(ServerLevel level, BlockPos pos, int radius) {
        for (Tracked t : BOSSES.values()) {
            LivingEntity b = t.boss;
            if (b.isAlive() && b.level() == level
                    && b.blockPosition().closerThan(pos, radius)) {
                return true;
            }
        }
        return false;
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            var it = BOSSES.values().iterator();
            while (it.hasNext()) {
                Tracked t = it.next();
                LivingEntity b = t.boss;
                if (!b.isAlive()) {
                    t.bar.removeAllPlayers();
                    server.getCustomBossEvents().remove(t.bar);
                    it.remove();
                    continue;
                }
                float max = b.getMaxHealth();
                t.bar.setProgress(max > 0 ? b.getHealth() / max : 0);
                for (var p : ((ServerLevel) b.level()).players()) {
                    if (p.blockPosition().closerThan(b.blockPosition(), 64)) {
                        t.bar.addPlayer(p);
                    }
                }
                runPhases(server, t, b, max);
            }
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!entity.entityTags().contains("bcn_boss")) return;
            if (!(entity.level() instanceof ServerLevel level)) return;
            Tracked t = BOSSES.get(entity.getUUID());
            if (t != null && t.altarPos != null) {
                BlockEntity be = level.getBlockEntity(t.altarPos);
                if (be instanceof com.buildcraftneo.industry.BossAltarBlockEntity altar) {
                    altar.onBossKill(t.bossIndex);
                }
            }
            Block.popResource(level, entity.blockPosition(),
                    new ItemStack(Items.DIAMOND, 3));
            Block.popResource(level, entity.blockPosition(),
                    new ItemStack(com.buildcraftneo.item.BCNItems.GOLD_COIN, 5));
            Block.popResource(level, entity.blockPosition(),
                    new ItemStack(Items.NETHERITE_SCRAP, 1));
            // Twilight Forest-style boss-specific rewards
            if (t != null && t.bossIndex == 1) {
                Block.popResource(level, entity.blockPosition(),
                        new ItemStack(com.buildcraftneo.item.BCNItems.NAGA_SCALE, 4));
            } else if (t != null && t.bossIndex == 2) {
                Block.popResource(level, entity.blockPosition(),
                        new ItemStack(com.buildcraftneo.item.BCNItems.LICH_SCEPTER, 1));
                Block.popResource(level, entity.blockPosition(),
                        new ItemStack(com.buildcraftneo.item.BCNItems.LICH_CROWN, 1));
            }
        });
    }

    /** Cataclysm-style phase attacks driven off the tracked boss' HP. */
    private static void runPhases(MinecraftServer server, Tracked t, LivingEntity b, float max) {
        if (!(b.level() instanceof ServerLevel level)) return;
        float hp = max > 0 ? b.getHealth() / max : 1f;

        // periodic AoE smash every 80 ticks
        if (level.getGameTime() % 80 == 0) {
            aoeSmash(level, b, 5.0, 6.0f, false);
        }
        // ranged fireball volley every 60 ticks when a player is in 4-24 blocks
        if (level.getGameTime() % 60 == 20) {
            fireballVolley(level, b);
        }
        // bullet-hell phase (<75% HP): radial fireball ring every 160 ticks
        if (hp < 0.75f && level.getGameTime() % 160 == 40) {
            fireballRing(level, b);
        }
        // fang phase (<60% HP): evoker-fang ring erupts around the nearest
        // player every 140 ticks
        if (hp < 0.6f && level.getGameTime() % 140 == 70) {
            fangRing(level, b);
        }
        // thunder phase (<40% HP): call down a lightning bolt on the nearest
        // player every 200 ticks, like the Monstrosity's storm attacks
        if (hp < 0.4f && level.getGameTime() % 200 == 100) {
            thunderCall(level, b);
        }
        // phase 2 (<50% HP): summon two minions, once
        if (hp < 0.5f && (t.phaseFlags & 1) == 0) {
            t.phaseFlags |= 1;
            for (int i = 0; i < 2; i++) {
                var minion = new WitherSkeleton(EntityTypes.WITHER_SKELETON, level);
                minion.setPos(b.getX() + (i == 0 ? 1.5 : -1.5), b.getY(), b.getZ() + 1.5);
                minion.setCustomName(Component.literal("Monstrosity Minion"));
                minion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                minion.setPersistenceRequired();
                minion.addTag("bcn_minion");
                level.addFreshEntity(minion);
            }
        }
        // phase 3 (<25% HP): one-time big burst + ignite, then meteors keep
        // raining on the nearest player for the rest of the fight
        if (hp < 0.25f && (t.phaseFlags & 2) == 0) {
            t.phaseFlags |= 2;
            aoeSmash(level, b, 8.0, 12.0f, true);
        }
        if (hp < 0.25f && level.getGameTime() % 120 == 60) {
            meteorStrike(level, b);
        }
    }

    /**
     * Cataclysm-style phase-3 continuous attack: fireballs rain down on the
     * nearest player from above while the boss is below 25% HP.
     */
    private static void meteorStrike(ServerLevel level, LivingEntity boss) {
        var box = boss.getBoundingBox().inflate(24.0);
        var players = level.getEntitiesOfClass(
                net.minecraft.server.level.ServerPlayer.class, box,
                p -> p.isAlive() && !p.isSpectator());
        if (players.isEmpty()) return;
        var target = players.getFirst();
        double best = target.distanceToSqr(boss);
        for (var p : players) {
            if (p.distanceToSqr(boss) < best) {
                best = p.distanceToSqr(boss);
                target = p;
            }
        }
        for (int i = 0; i < 3; i++) {
            double ox = (level.getRandom().nextDouble() - 0.5) * 4.0;
            double oz = (level.getRandom().nextDouble() - 0.5) * 4.0;
            var fb = new net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball(
                    level, boss, new Vec3(0, -1.5, 0));
            fb.setPos(target.getX() + ox, target.getY() + 6.0, target.getZ() + oz);
            level.addFreshEntity(fb);
        }
    }

    /** Cataclysm-style ranged attack: lob a small fireball at the nearest player. */
    private static void fireballVolley(ServerLevel level, LivingEntity boss) {
        var box = boss.getBoundingBox().inflate(24.0);
        var players = level.getEntitiesOfClass(
                net.minecraft.server.level.ServerPlayer.class, box,
                p -> p.isAlive() && p.blockPosition().distSqr(boss.blockPosition()) >= 16.0);
        if (players.isEmpty()) return;
        var target = players.get(level.getRandom().nextInt(players.size()));
        Vec3 aim = new Vec3(target.getX() - boss.getX(),
                target.getEyeY() - boss.getEyeY(), target.getZ() - boss.getZ()).normalize();
        var fb = new net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball(
                level, boss, aim.scale(1.5));
        fb.setPos(boss.getX() + aim.x * 1.5, boss.getEyeY(), boss.getZ() + aim.z * 1.5);
        level.addFreshEntity(fb);
    }

    /**
     * Cataclysm-style fang phase: while below 60% HP the boss periodically
     * erupts a ring of evoker fangs around the nearest player, like the
     * Evoker's close-range attack.
     */
    private static void fangRing(ServerLevel level, LivingEntity boss) {
        var box = boss.getBoundingBox().inflate(20.0);
        var players = level.getEntitiesOfClass(
                net.minecraft.server.level.ServerPlayer.class, box,
                p -> p.isAlive() && !p.isSpectator());
        if (players.isEmpty()) return;
        var target = players.getFirst();
        double best = target.distanceToSqr(boss);
        for (var p : players) {
            if (p.distanceToSqr(boss) < best) {
                best = p.distanceToSqr(boss);
                target = p;
            }
        }
        int count = 8;
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2 * i / count;
            double fx = target.getX() + Math.cos(angle) * 2.5;
            double fz = target.getZ() + Math.sin(angle) * 2.5;
            var fang = new net.minecraft.world.entity.projectile.EvokerFangs(
                    level, fx, target.getY(), fz, (float) angle, i, boss);
            level.addFreshEntity(fang);
        }
    }

    /**
     * Cataclysm-style thunder phase: while below 40% HP the boss periodically
     * calls a real lightning bolt down on the nearest player within range.
     */
    private static void thunderCall(ServerLevel level, LivingEntity boss) {
        var box = boss.getBoundingBox().inflate(24.0);
        var players = level.getEntitiesOfClass(
                net.minecraft.server.level.ServerPlayer.class, box,
                p -> p.isAlive() && !p.isSpectator());
        if (players.isEmpty()) return;
        var target = players.getFirst();
        double best = target.distanceToSqr(boss);
        for (var p : players) {
            if (p.distanceToSqr(boss) < best) {
                best = p.distanceToSqr(boss);
                target = p;
            }
        }
        var bolt = new net.minecraft.world.entity.LightningBolt(
                net.minecraft.world.entity.EntityTypes.LIGHTNING_BOLT, level);
        bolt.setPos(target.getX(), target.getY(), target.getZ());
        level.addFreshEntity(bolt);
    }

    /** Cataclysm-style bullet phase: a ring of fireballs in every direction. */
    private static void fireballRing(ServerLevel level, LivingEntity boss) {
        int count = 12;
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2 * i / count;
            Vec3 dir = new Vec3(Math.cos(angle), -0.05, Math.sin(angle));
            var fb = new net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball(
                    level, boss, dir.scale(1.2));
            fb.setPos(boss.getX() + dir.x * 1.5, boss.getEyeY(),
                    boss.getZ() + dir.z * 1.5);
            level.addFreshEntity(fb);
        }
    }

    private static void aoeSmash(ServerLevel level, LivingEntity boss, double radius,
            float damage, boolean ignite) {
        AABB box = boss.getBoundingBox().inflate(radius);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != boss && e.isAlive() && !e.entityTags().contains("bcn_boss")
                        && !e.entityTags().contains("bcn_minion"))) {
            e.hurtServer(level, level.damageSources().mobAttack(boss), damage);
            double dx = e.getX() - boss.getX();
            double dz = e.getZ() - boss.getZ();
            double len = Math.max(0.1, Math.sqrt(dx * dx + dz * dz));
            e.push(dx / len * 1.2, 0.4, dz / len * 1.2);
            if (ignite) e.igniteForSeconds(4.0f);
        }
    }
}
