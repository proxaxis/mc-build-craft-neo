package com.buildcraftneo.data;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class Profiles {
    private Profiles() {}

    public static Faction faction(ServerPlayer player) {
        return Faction.byName(player.getAttachedOrCreate(BCNAttachments.FACTION));
    }

    public static void setFaction(ServerPlayer player, Faction faction) {
        player.setAttached(BCNAttachments.FACTION, faction.id());
        // keep job compatible with faction
        if (!job(player).isAllowedFor(faction)) {
            setJob(player, faction == Faction.SCIENCE ? Job.SCIENTIST : Job.NONE);
        }
    }

    public static Job job(ServerPlayer player) {
        return Job.byName(player.getAttachedOrCreate(BCNAttachments.JOB));
    }

    public static void setJob(ServerPlayer player, Job job) {
        player.setAttached(BCNAttachments.JOB, job.id());
    }

    public static long md(ServerPlayer player) {
        return player.getAttachedOrCreate(BCNAttachments.MD);
    }

    public static void setMd(ServerPlayer player, long amount) {
        player.setAttached(BCNAttachments.MD, Math.max(0L, amount));
    }

    public static long addMd(ServerPlayer player, long delta) {
        long next = Math.max(0L, md(player) + delta);
        setMd(player, next);
        return next;
    }

    public static int mp(ServerPlayer player) {
        return player.getAttachedOrCreate(BCNAttachments.MP);
    }

    public static int mpMax(ServerPlayer player) {
        return player.getAttachedOrCreate(BCNAttachments.MP_MAX);
    }

    public static void setMp(ServerPlayer player, int value) {
        player.setAttached(BCNAttachments.MP, Math.clamp(value, 0, mpMax(player)));
    }

    public static int addMp(ServerPlayer player, int delta) {
        int next = Math.clamp(mp(player) + delta, 0, mpMax(player));
        player.setAttached(BCNAttachments.MP, next);
        return next;
    }

    /** True if the player had enough MP; on success the cost is consumed. */
    public static boolean consumeMp(ServerPlayer player, int cost) {
        if (mp(player) < cost) return false;
        setMp(player, mp(player) - cost);
        return true;
    }

    // ---- world state ----

    public static double efp(ServerLevel overworld) {
        return overworld.getAttachedOrCreate(BCNAttachments.EFP);
    }

    public static double addEfp(ServerLevel overworld, double delta) {
        double next = Math.max(0.0, efp(overworld) + delta);
        overworld.setAttached(BCNAttachments.EFP, next);
        return next;
    }

    public static double efpOwn(ServerPlayer player) {
        return player.getAttachedOrCreate(BCNAttachments.EFP_OWN);
    }

    public static long mjGenerated(ServerLevel level) {
        return level.getAttachedOrCreate(BCNAttachments.MJ_GENERATED);
    }

    public static long addMjGenerated(ServerLevel level, long delta) {
        long next = mjGenerated(level) + delta;
        level.setAttached(BCNAttachments.MJ_GENERATED, next);
        return next;
    }
}
