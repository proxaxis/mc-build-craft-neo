package com.buildcraftneo.economy;

import com.buildcraftneo.data.BCNAttachments;
import com.buildcraftneo.data.Profiles;
import com.buildcraftneo.test.TestFakes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Global MD auction house. Offers and pending seller payouts are persisted
 * on the overworld via attachments. Items are escrowed in the offer itself.
 */
public final class AuctionHouse {
    private AuctionHouse() {}

    public record Offer(int id, String seller, ItemStack item, long price) {
        public static final Codec<Offer> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("id").forGetter(Offer::id),
                Codec.STRING.fieldOf("seller").forGetter(Offer::seller),
                ItemStack.CODEC.fieldOf("item").forGetter(Offer::item),
                Codec.LONG.fieldOf("price").forGetter(Offer::price)
        ).apply(i, Offer::new));
    }

    public static List<Offer> offers(ServerLevel overworld) {
        return List.copyOf(overworld.getAttachedOrCreate(BCNAttachments.AUCTIONS));
    }

    /** Put the given stack up for sale; returns the offer id. */
    public static int sell(ServerLevel overworld, ServerPlayer seller, ItemStack stack, long price) {
        List<Offer> offers = new ArrayList<>(overworld.getAttachedOrCreate(BCNAttachments.AUCTIONS));
        int id = offers.stream().mapToInt(Offer::id).max().orElse(0) + 1;
        offers.add(new Offer(id, seller.getName().getString(), stack.copy(), price));
        overworld.setAttached(BCNAttachments.AUCTIONS, offers);
        return id;
    }

    /**
     * Buy an offer: transfers MD to the seller (or to pending payouts if the
     * seller cannot be resolved) and the item to the buyer.
     * Returns a human-readable result string.
     */
    public static String buy(ServerLevel overworld, ServerPlayer buyer, int id) {
        List<Offer> offers = new ArrayList<>(overworld.getAttachedOrCreate(BCNAttachments.AUCTIONS));
        Offer offer = null;
        for (Offer o : offers) if (o.id() == id) offer = o;
        if (offer == null) return "no offer #" + id;
        if (Profiles.md(buyer) < offer.price()) {
            return "insufficient MD (have " + Profiles.md(buyer) + ", need " + offer.price() + ")";
        }
        offers.remove(offer);
        overworld.setAttached(BCNAttachments.AUCTIONS, offers);
        Profiles.addMd(buyer, -offer.price());
        paySeller(overworld, offer.seller(), offer.price());
        ItemStack item = offer.item().copy();
        if (!buyer.getInventory().add(item)) {
            buyer.drop(item, false, net.minecraft.util.Prediction.SERVER_ONLY);
        }
        return "bought #" + id + " " + describe(offer) + " for " + offer.price() + " MD";
    }

    /** Pay pending earnings to a player; returns amount paid. */
    public static long collect(ServerLevel overworld, ServerPlayer player) {
        Map<String, Long> pending = new HashMap<>(
                overworld.getAttachedOrCreate(BCNAttachments.AUCTION_EARNINGS));
        Long amount = pending.remove(player.getName().getString());
        if (amount == null || amount <= 0) return 0;
        overworld.setAttached(BCNAttachments.AUCTION_EARNINGS, pending);
        Profiles.addMd(player, amount);
        return amount;
    }

    private static void paySeller(ServerLevel overworld, String seller, long price) {
        ServerPlayer p = overworld.getServer().getPlayerList().getPlayerByName(seller);
        if (p == null) p = TestFakes.get(seller);
        if (p != null) {
            Profiles.addMd(p, price);
            return;
        }
        Map<String, Long> pending = new HashMap<>(
                overworld.getAttachedOrCreate(BCNAttachments.AUCTION_EARNINGS));
        pending.merge(seller, price, Long::sum);
        overworld.setAttached(BCNAttachments.AUCTION_EARNINGS, pending);
    }

    public static String describe(Offer o) {
        return o.item().getCount() + "x" + BuiltInRegistries.ITEM.getKey(o.item().getItem())
                + " @" + o.price() + "MD by " + o.seller();
    }
}
