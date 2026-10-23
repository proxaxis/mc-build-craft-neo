package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Base class: item container + MJ buffer. */
public abstract class MachineBlockEntity extends BlockEntity implements Container, Mj.Consumer {
    protected final NonNullList<ItemStack> items;
    protected long mj;

    protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slots) {
        super(type, pos, state);
        this.items = NonNullList.withSize(slots, ItemStack.EMPTY);
    }

    // ---- MJ ----

    public long mjCapacity() {
        return 5000;
    }

    @Override
    public long accept(long amount) {
        long free = mjCapacity() - mj;
        long taken = Math.min(free, Math.max(0, amount));
        mj += taken;
        if (taken > 0) setChanged();
        return amount - taken;
    }

    @Override
    public long stored() {
        return mj;
    }

    @Override
    public long capacity() {
        return mjCapacity();
    }

    protected boolean consumeMj(long amount) {
        if (mj < amount) return false;
        mj -= amount;
        setChanged();
        return true;
    }

    // ---- Container ----

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : items) if (!s.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack split = ContainerHelper.removeItem(items, slot, amount);
        if (!split.isEmpty()) setChanged();
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    // ---- persistence ----

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        ContainerHelper.saveAllItems(out, items);
        out.putLong("mj", mj);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        ContainerHelper.loadAllItems(in, items);
        mj = in.getLongOr("mj", 0);
    }

    /** One-line status for chat / rcon inspection. */
    public String status() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append(" mj=").append(mj).append("/").append(mjCapacity()).append(" items=[");
        for (int i = 0; i < items.size(); i++) {
            ItemStack s = items.get(i);
            if (!s.isEmpty()) {
                sb.append(i).append(":").append(s.getCount()).append("x")
                        .append(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(s.getItem()))
                        .append(" ");
            }
        }
        return sb.append(']').toString();
    }
}
