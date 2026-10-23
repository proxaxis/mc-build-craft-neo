package com.buildcraftneo.disaster;

import com.buildcraftneo.BuildCraftNeo;
import com.buildcraftneo.data.Profiles;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * EFP-triggered raid event: when the global environmental footprint exceeds
 * the threshold, an armed raid wave spawns around the world spawn.
 */
public final class DisasterManager {
    private DisasterManager() {}

    private static final int CHECK_PERIOD = 200;
    private static final long COOLDOWN_TICKS = 12000; // 10 min

    private static int threshold = 1000;
    private static long cooldownUntil = 0;

    public static int threshold() {
        return threshold;
    }

    public static void setThreshold(int value) {
        threshold = Math.max(1, value);
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % CHECK_PERIOD != 0) return;
            ServerLevel level = server.overworld();
            if (Profiles.efp(level) >= threshold && server.getTickCount() >= cooldownUntil) {
                triggerRaid(server);
            }
        });
    }

    /** Spawn the raid; returns number of hostiles spawned. */
    public static int triggerRaid(MinecraftServer server) {
        ServerLevel level = server.overworld();
        BlockPos spawn = level.getRespawnData().pos();
        int spawned = 0;

        for (int i = 0; i < 4; i++) spawned += spawn(level, raidPos(level, spawn, i), new Zombie(level));
        for (int i = 0; i < 3; i++) spawned += spawn(level, raidPos(level, spawn, i + 4), new Skeleton(net.minecraft.world.entity.EntityTypes.SKELETON, level));
        for (int i = 0; i < 2; i++) spawned += spawn(level, raidPos(level, spawn, i + 7), new Pillager(net.minecraft.world.entity.EntityTypes.PILLAGER, level));

        double newEfp = Profiles.addEfp(level, -threshold * 0.5);
        cooldownUntil = server.getTickCount() + COOLDOWN_TICKS;

        server.getPlayerList().broadcastSystemMessage(Component.literal(
                "§c[災害] 環境負荷が限界を超え、襲撃部隊が襲来しました！ (spawned=" + spawned + ")"), false);
        BuildCraftNeo.LOGGER.info("[BuildCraftNeo] disaster raid spawned={} efp->{}", spawned, newEfp);
        return spawned;
    }

    private static BlockPos raidPos(ServerLevel level, BlockPos center, int index) {
        double angle = index * (Math.PI * 2 / 9);
        int x = center.getX() + (int) (Math.cos(angle) * 20);
        int z = center.getZ() + (int) (Math.sin(angle) * 20);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, Math.max(y, level.getMinY() + 1), z);
    }

    private static int spawn(ServerLevel level, BlockPos pos, Monster mob) {
        mob.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (mob instanceof Zombie) {
            mob.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,
                    new ItemStack(Items.IRON_SWORD));
        }
        level.addFreshEntity(mob);
        return 1;
    }
}
