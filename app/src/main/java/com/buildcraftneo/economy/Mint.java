package com.buildcraftneo.economy;

import com.buildcraftneo.data.Profiles;
import com.buildcraftneo.item.BCNItems;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Coin <-> MD conversion (Lightman's Currency analogue). */
public final class Mint {
    private Mint() {}

    /** Convert all coin items in the player's inventory into MD. Returns deposited amount. */
    public static long depositAll(ServerPlayer player) {
        long deposited = 0;
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            Long value = BCNItems.COIN_VALUES.get(s.getItem());
            if (value != null) {
                deposited += value * s.getCount();
                inv.removeItemNoUpdate(i);
            }
        }
        if (deposited > 0) {
            Profiles.addMd(player, deposited);
        }
        return deposited;
    }

    /** Withdraw amount as coins (largest denominations first). Returns actual withdrawn. */
    public static long withdraw(ServerPlayer player, long amount) {
        long balance = Profiles.md(player);
        long actual = Math.min(balance, amount);
        if (actual <= 0) return 0;

        List<Map.Entry<Item, Long>> tiers = new ArrayList<>(BCNItems.COIN_VALUES.entrySet());
        tiers.sort(Comparator.comparingLong(e -> -e.getValue()));

        long remaining = actual;
        for (var tier : tiers) {
            long v = tier.getValue();
            long count = remaining / v;
            if (count > 0) {
                remaining -= giveOrDrop(player, tier.getKey(), count);
            }
        }
        Profiles.addMd(player, -(actual - remaining));
        return actual - remaining;
    }

    private static long giveOrDrop(ServerPlayer player, Item coin, long count) {
        int max = coin.getDefaultMaxStackSize();
        long given = 0;
        while (count > 0) {
            int n = (int) Math.min(count, max);
            ItemStack stack = new ItemStack(coin, n);
            if (player.getInventory().add(stack)) {
                given += n;
            } else {
                player.drop(stack, false, net.minecraft.util.Prediction.SERVER_ONLY);
                given += n; // dropped at feet still counts as paid out
            }
            count -= n;
        }
        return given;
    }
}
