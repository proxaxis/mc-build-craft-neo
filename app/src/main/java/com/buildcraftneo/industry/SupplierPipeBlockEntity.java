package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Supplier pipe (LogisticsPipes analogue): keeps adjacent containers stocked
 * with the configured item up to the configured threshold, pulling missing
 * items from the connected pipe network.
 */
public class SupplierPipeBlockEntity extends MachineBlockEntity {
    public static final int PERIOD = 40;

    private Item item;
    private int keep = 16;
    private ItemStack transit = ItemStack.EMPTY;
    private String lastAct = "-";

    public SupplierPipeBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.SUPPLIER_PIPE, pos, state, 0);
    }

    public void setSupply(Item item, int keep) {
        this.item = item;
        this.keep = Math.max(1, keep);
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SupplierPipeBlockEntity self) {
        if (level.isClientSide() || level.getGameTime() % PERIOD != 0) return;

        if (!self.transit.isEmpty()) {
            self.transit = PipeNetwork.depositAdjacent(level, pos, self.transit);
            self.setChanged();
            return;
        }
        if (self.item == null) return;

        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (!(be instanceof Container container) || PipeNetwork.isPipe(be)) continue;
            int need = self.keep - countIn(container, self.item);
            if (need <= 0) continue;
            ItemStack pulled = PipeNetwork.pullFromNetwork(level, pos, self.item, Math.min(need, 32));
            if (!pulled.isEmpty()) {
                ItemStack rem = ItemPipeBlockEntity.insert(container, pulled);
                self.lastAct = "supplied " + (pulled.getCount() - rem.getCount()) + "x -> " + dir;
                if (!rem.isEmpty()) self.transit = rem;
                self.setChanged();
                return;
            }
            self.lastAct = "no-source need=" + need;
        }
    }

    private static int countIn(Container container, Item item) {
        int n = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty() && s.is(item)) n += s.getCount();
        }
        return n;
    }

    @Override
    public String status() {
        return super.status() + " supply=" + (item == null ? "-" : BuiltInRegistries.ITEM.getKey(item))
                + " keep=" + keep + " last=" + lastAct;
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        if (item != null) out.putString("item", BuiltInRegistries.ITEM.getKey(item).toString());
        out.putInt("keep", keep);
        if (!transit.isEmpty()) out.store("transit", ItemStack.CODEC, transit);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        String s = in.getStringOr("item", "");
        item = s.isEmpty() ? null : BuiltInRegistries.ITEM.getValue(Identifier.parse(s));
        keep = in.getIntOr("keep", 16);
        transit = in.read("transit", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
