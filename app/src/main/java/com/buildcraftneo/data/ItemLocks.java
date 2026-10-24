package com.buildcraftneo.data;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Slot locks (ItemLocks analogue): a locked inventory slot keeps a snapshot of
 * its stack; the server restores the snapshot whenever the locked slot's item
 * disappears or shrinks (drop / move protection approximation).
 */
public final class ItemLocks {
    private ItemLocks() {}

    private static final int PERIOD = 10;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % PERIOD != 0) return;
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                enforce(p);
            }
            for (ServerPlayer p : com.buildcraftneo.test.TestFakes.all()) {
                enforce(p);
            }
        });
    }

    private static Map<String, ItemStack> locks(ServerPlayer p) {
        return new HashMap<>(p.getAttachedOrCreate(BCNAttachments.LOCKED));
    }

    /** Toggle lock on an inventory slot. Returns a description of the new state. */
    public static String toggle(ServerPlayer p, int slot) {
        var inv = p.getInventory();
        if (slot < 0 || slot >= inv.getContainerSize()) return "invalid slot " + slot;
        Map<String, ItemStack> map = locks(p);
        String key = String.valueOf(slot);
        if (map.containsKey(key)) {
            map.remove(key);
            p.setAttached(BCNAttachments.LOCKED, Map.copyOf(map));
            return "unlocked slot " + slot;
        }
        ItemStack cur = inv.getItem(slot);
        if (cur.isEmpty()) return "slot " + slot + " is empty (nothing to lock)";
        map.put(key, cur.copy());
        p.setAttached(BCNAttachments.LOCKED, Map.copyOf(map));
        return "locked slot " + slot + " -> " + cur.getCount() + "x "
                + BuiltInRegistries.ITEM.getKey(cur.getItem());
    }

    public static boolean isLocked(ServerPlayer p, int slot) {
        return p.getAttachedOrCreate(BCNAttachments.LOCKED).containsKey(String.valueOf(slot));
    }

    public static String describe(ServerPlayer p) {
        Map<String, ItemStack> map = p.getAttachedOrCreate(BCNAttachments.LOCKED);
        if (map.isEmpty()) return "-";
        StringBuilder sb = new StringBuilder();
        for (var e : map.entrySet()) {
            sb.append(e.getKey()).append(':')
                    .append(BuiltInRegistries.ITEM.getKey(e.getValue().getItem())).append(' ');
        }
        return sb.toString().trim();
    }

    private static void enforce(ServerPlayer p) {
        Map<String, ItemStack> map = p.getAttachedOrCreate(BCNAttachments.LOCKED);
        if (map.isEmpty()) return;
        var inv = p.getInventory();
        for (var e : map.entrySet()) {
            int slot;
            try {
                slot = Integer.parseInt(e.getKey());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (slot < 0 || slot >= inv.getContainerSize()) continue;
            ItemStack want = e.getValue();
            ItemStack cur = inv.getItem(slot);
            if (cur.isEmpty() || !ItemStack.isSameItemSameComponents(cur, want)
                    || cur.getCount() < want.getCount()) {
                inv.setItem(slot, want.copy());
            }
        }
    }
}
