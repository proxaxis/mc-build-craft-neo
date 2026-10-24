package com.buildcraftneo.industry;

import com.buildcraftneo.data.Profiles;
import com.buildcraftneo.item.BCNItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * ATM deposit menu (Lightman's Currency analogue): 9 deposit slots on top,
 * player inventory below. Coins placed into the deposit slots are converted
 * to MD when the menu closes; anything else is returned to the player.
 */
public class AtmMenu extends AbstractContainerMenu {
    public static final int DEPOSIT_SLOTS = 9;

    private final Container depositInv;
    private final net.minecraft.core.BlockPos pos;

    /** MenuSupplier constructor (client side). */
    public AtmMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainer(DEPOSIT_SLOTS), null);
    }

    public AtmMenu(int id, Inventory inv, Container depositInv,
            net.minecraft.core.BlockPos pos) {
        super(BCNMenus.ATM, id);
        this.depositInv = depositInv;
        this.pos = pos;
        for (int i = 0; i < DEPOSIT_SLOTS; i++) {
            addSlot(new Slot(depositInv, i, 8 + i * 18, 20));
        }
        addStandardInventorySlots(inv, 8, 20 + 18 + 14);
    }

    public int depositSlots() {
        return DEPOSIT_SLOTS;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int idx) {
        Slot slot = slots.get(idx);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (idx < DEPOSIT_SLOTS) {
            if (!moveItemStackTo(stack, DEPOSIT_SLOTS, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, DEPOSIT_SLOTS, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return depositInv.stillValid(player)
                && (pos == null || player.blockPosition().closerThan(pos, 8.0));
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!(player instanceof net.minecraft.server.level.ServerPlayer sp)) return;
        long deposited = 0;
        for (int i = 0; i < DEPOSIT_SLOTS; i++) {
            ItemStack s = depositInv.removeItemNoUpdate(i);
            if (s.isEmpty()) continue;
            Long value = BCNItems.COIN_VALUES.get(s.getItem());
            if (value != null) {
                deposited += value * s.getCount();
            } else {
                player.getInventory().placeItemBackInInventory(s,
                        net.minecraft.util.Prediction.SERVER_ONLY);
            }
        }
        if (deposited > 0) {
            Profiles.addMd(sp, deposited);
        }
        sp.sendSystemMessage(Component.literal(
                "[BCN ATM] 入金 " + deposited + " MD (残高=" + Profiles.md(sp) + ")"));
    }
}
