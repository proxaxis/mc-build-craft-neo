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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

/**
 * Tracks spawned dungeon bosses: drives a custom boss bar (visible in
 * `bossbar list`) that follows nearby players and boss HP, and drops
 * bonus loot on boss death.
 */
public final class BossManager {
    private BossManager() {}

    private record Tracked(LivingEntity boss, CustomBossEvent bar) {}

    private static final Map<java.util.UUID, Tracked> BOSSES = new ConcurrentHashMap<>();

    public static void track(LivingEntity boss, MinecraftServer server) {
        CustomBossEvent bar = server.getCustomBossEvents().create(
                server.overworld().getRandom(),
                BCN.id("boss_" + boss.getUUID().toString().replace("-", "").substring(0, 12)),
                Component.literal(boss.getName().getString()));
        bar.setColor(BossEvent.BossBarColor.RED);
        bar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_10);
        BOSSES.put(boss.getUUID(), new Tracked(boss, bar));
    }

    public static boolean bossAliveAt(ServerLevel level, BlockPos pos, int radius) {
        for (Tracked t : BOSSES.values()) {
            LivingEntity b = t.boss();
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
                LivingEntity b = t.boss();
                if (!b.isAlive()) {
                    t.bar().removeAllPlayers();
                    server.getCustomBossEvents().remove(t.bar());
                    it.remove();
                    continue;
                }
                float max = b.getMaxHealth();
                t.bar().setProgress(max > 0 ? b.getHealth() / max : 0);
                for (var p : ((ServerLevel) b.level()).players()) {
                    if (p.blockPosition().closerThan(b.blockPosition(), 64)) {
                        t.bar().addPlayer(p);
                    }
                }
            }
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!entity.entityTags().contains("bcn_boss")) return;
            Entity e = entity;
            if (!(e.level() instanceof ServerLevel level)) return;
            Block.popResource(level, entity.blockPosition(),
                    new ItemStack(Items.DIAMOND, 3));
            Block.popResource(level, entity.blockPosition(),
                    new ItemStack(com.buildcraftneo.item.BCNItems.GOLD_COIN, 5));
            Block.popResource(level, entity.blockPosition(),
                    new ItemStack(Items.NETHERITE_SCRAP, 1));
        });
    }
}
