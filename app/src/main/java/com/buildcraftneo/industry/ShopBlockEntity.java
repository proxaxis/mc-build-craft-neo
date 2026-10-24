package com.buildcraftneo.industry;

import com.buildcraftneo.data.Profiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Shop block: sells a configured item for MD. Right-click buys one item;
 * sneak-right-click shows status. Stock is the 27-slot internal inventory,
 * earnings accumulate as MD and are collected by the owner.
 */
public class ShopBlockEntity extends MachineBlockEntity {
    public static final int SLOTS = 27;

    private Item offer;
    private long price;
    private long earnings;
    private String owner = "";
    /** Last N sale lines (volatile - not persisted). */
    private final java.util.ArrayDeque<String> history = new java.util.ArrayDeque<>();
    /** Bumped whenever history changes; lets menus resend only on change. */
    private int historyVersion;

    public ShopBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.SHOP, pos, state, SLOTS);
    }

    public void setOffer(Item item, long price, String owner) {
        this.offer = item;
        this.price = Math.max(0, price);
        this.owner = owner == null ? "" : owner;
        setChanged();
    }

    public boolean isSelling() {
        return offer != null && price > 0;
    }

    public int stock() {
        int n = 0;
        for (ItemStack s : items) if (!s.isEmpty() && s.is(offer)) n += s.getCount();
        return n;
    }

    /** Sell one item to the player. Returns a human-readable result. */
    public String sellTo(ServerPlayer p) {
        if (!isSelling()) return "shop has no offer";
        if (Profiles.md(p) < price) {
            return "insufficient MD (have " + Profiles.md(p) + ", need " + price + ")";
        }
        for (int i = 0; i < items.size(); i++) {
            ItemStack s = items.get(i);
            if (!s.isEmpty() && s.is(offer)) {
                ItemStack one = removeItem(i, 1);
                Profiles.addMd(p, -price);
                earnings += price;
                if (!p.getInventory().add(one)) {
                    p.drop(one, false, net.minecraft.util.Prediction.SERVER_ONLY);
                }
                if (history.size() >= 10) history.pollFirst();
                history.addLast(p.getName().getString() + " bought 1x"
                        + BuiltInRegistries.ITEM.getKey(offer) + " for " + price + " MD");
                historyVersion++;
                setChanged();
                return "sold 1x" + BuiltInRegistries.ITEM.getKey(offer)
                        + " for " + price + " MD";
            }
        }
        return "out of stock";
    }

    /** Pay accumulated earnings to the player. Returns amount paid, or -1 if not allowed. */
    public long collectTo(ServerPlayer p) {
        if (!owner.isEmpty() && !p.getName().getString().equals(owner)
                && !p.createCommandSourceStack().permissions().hasPermission(
                        new net.minecraft.server.permissions.Permission.HasCommandLevel(
                                net.minecraft.server.permissions.PermissionLevel.GAMEMASTERS))) {
            return -1;
        }
        long e = earnings;
        earnings = 0;
        Profiles.addMd(p, e);
        setChanged();
        return e;
    }

    public java.util.List<String> history() {
        return java.util.List.copyOf(history);
    }

    public int historyVersion() {
        return historyVersion;
    }

    public String offerId() {
        return offer == null ? "-" : BuiltInRegistries.ITEM.getKey(offer).toString();
    }

    @Override
    public String status() {
        return super.status() + " offer=" + offerId() + " price=" + price
                + " stock=" + stock() + " earned=" + earnings
                + " owner=" + (owner.isEmpty() ? "-" : owner);
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        if (offer != null) out.putString("offer", BuiltInRegistries.ITEM.getKey(offer).toString());
        out.putLong("price", price);
        out.putLong("earnings", earnings);
        out.putString("owner", owner);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        String o = in.getStringOr("offer", "");
        offer = o.isEmpty() ? null
                : BuiltInRegistries.ITEM.getValue(Identifier.parse(o));
        price = in.getLongOr("price", 0);
        earnings = in.getLongOr("earnings", 0);
        owner = in.getStringOr("owner", "");
    }
}
