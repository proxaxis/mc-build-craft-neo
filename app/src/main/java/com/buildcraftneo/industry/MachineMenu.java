package com.buildcraftneo.industry;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Generic machine menu: exposes a machine's item slots (up to 9) on top and
 * the player inventory below. Works for any MachineBlockEntity.
 */
public class MachineMenu extends AbstractContainerMenu {
    private final Container machineInv;
    private final net.minecraft.core.BlockPos pos;
    private final DataSlot mjPct = DataSlot.standalone();
    private final DataSlot progPct = DataSlot.standalone();

    /** MenuSupplier constructor (client side). */
    public MachineMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainer(9), null);
    }

    public MachineMenu(int id, Inventory inv, Container machineInv,
            net.minecraft.core.BlockPos pos) {
        super(BCNMenus.MACHINE, id);
        this.machineInv = machineInv;
        this.pos = pos;
        int n = Math.min(machineInv.getContainerSize(), 9);
        for (int i = 0; i < n; i++) {
            addSlot(new Slot(machineInv, i, 8 + i * 18, 20));
        }
        addStandardInventorySlots(inv, 8, 20 + 18 + 14);
        addDataSlot(mjPct);
        addDataSlot(progPct);
    }

    public int machineSlots() {
        return Math.min(machineInv.getContainerSize(), 9);
    }

    public int mjPercent() {
        return mjPct.get();
    }

    public int progressPercent() {
        return progPct.get();
    }

    @Override
    public void broadcastChanges() {
        if (machineInv instanceof MachineBlockEntity mbe) {
            mjPct.set((int) Math.min(100,
                    mbe.stored() * 100 / Math.max(1, mbe.mjCapacity())));
            progPct.set(mbe.progressPercent());
        }
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int idx) {
        Slot slot = slots.get(idx);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        int machineEnd = machineSlots();
        if (idx < machineEnd) {
            if (!moveItemStackTo(stack, machineEnd, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, machineEnd, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return machineInv.stillValid(player)
                && (pos == null || player.blockPosition().closerThan(pos, 8.0));
    }
}
