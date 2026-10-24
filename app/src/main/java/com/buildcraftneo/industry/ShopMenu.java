package com.buildcraftneo.industry;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Shop stock-management menu: 27 shop slots on top, player inventory below.
 * Server-side logic (slots, clicks, shift-click moves) is fully functional;
 * a client screen is not yet provided (see AGENTS.md).
 */
public class ShopMenu extends AbstractContainerMenu {
    private final Container shopInv;
    private final Inventory playerInv;
    private int lastHistoryVersion = -1;

    /** MenuSupplier constructor (client side): placeholder container. */
    public ShopMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainer(ShopBlockEntity.SLOTS), ContainerLevelAccess.NULL);
    }

    public ShopMenu(int id, Inventory inv, Container shopInv, ContainerLevelAccess access) {
        super(BCNMenus.SHOP, id);
        this.shopInv = shopInv;
        this.playerInv = inv;
        for (int i = 0; i < ShopBlockEntity.SLOTS; i++) {
            addSlot(new Slot(shopInv, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18));
        }
        addStandardInventorySlots(inv, 8, 18 + 3 * 18 + 14);
    }

    @Override
    public void broadcastChanges() {
        // push sale history to the open client screen whenever it changes
        if (shopInv instanceof ShopBlockEntity shop
                && shop.historyVersion() != lastHistoryVersion
                && playerInv.player instanceof net.minecraft.server.level.ServerPlayer sp) {
            lastHistoryVersion = shop.historyVersion();
            net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(sp,
                    new com.buildcraftneo.net.ShopHistoryPayload(
                            shop.getBlockPos(), shop.history()));
        }
        super.broadcastChanges();
    }

    public int shopSlots() {
        return ShopBlockEntity.SLOTS;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int idx) {
        Slot slot = slots.get(idx);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        int shopEnd = ShopBlockEntity.SLOTS;
        if (idx < shopEnd) {
            if (!moveItemStackTo(stack, shopEnd, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, shopEnd, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return shopInv.stillValid(player);
    }
}
