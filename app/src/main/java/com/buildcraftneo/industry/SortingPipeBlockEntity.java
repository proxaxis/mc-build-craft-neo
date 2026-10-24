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
 * Sorting pipe (LogisticsPipes analogue): extracts only the configured filter
 * item from adjacent containers and claims its adjacent containers as
 * destinations for that item only (non-matching items pass through to the rest
 * of the network).
 */
public class SortingPipeBlockEntity extends MachineBlockEntity {
    public static final int PERIOD = 10;
    public static final int BATCH = 8;

    private Item filter;
    private ItemStack transit = ItemStack.EMPTY;
    private BlockPos lastSource;
    private String lastAct = "-";

    public SortingPipeBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.SORTING_PIPE, pos, state, 0);
    }

    public void setFilter(Item item) {
        filter = item;
        setChanged();
    }

    public Item filter() {
        return filter;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SortingPipeBlockEntity self) {
        if (level.isClientSide() || level.getGameTime() % PERIOD != 0) return;

        if (!self.transit.isEmpty()) {
            self.transit = PipeNetwork.route(level, pos, self.transit, self.lastSource);
            self.setChanged();
            return;
        }

        for (Direction dir : Direction.values()) {
            BlockPos sourcePos = pos.relative(dir);
            BlockEntity be = level.getBlockEntity(sourcePos);
            if (be instanceof Container container && !PipeNetwork.isPipe(be)) {
                ItemStack taken = extractFiltered(container, BATCH, self.filter);
                if (!taken.isEmpty()) {
                    self.lastSource = sourcePos;
                    self.lastAct = "pull " + taken.getCount() + "x <- " + dir;
                    self.transit = PipeNetwork.route(level, pos, taken, sourcePos);
                    self.setChanged();
                    return;
                }
            }
        }
    }

    /**
     * Called when a routed stack passes through this pipe: insert into adjacent
     * non-pipe containers if the filter matches (or no filter is set).
     */
    public ItemStack tryRoute(ItemStack stack) {
        if (level == null || (filter != null && !stack.is(filter))) return stack;
        for (Direction dir : Direction.values()) {
            net.minecraft.core.BlockPos dst = getBlockPos().relative(dir);
            BlockEntity be = level.getBlockEntity(dst);
            if (be instanceof Container container && !PipeNetwork.isPipe(be)) {
                int before = stack.getCount();
                stack = ItemPipeBlockEntity.insert(container, stack);
                if (stack.getCount() != before) PipeNetwork.noteDelivery(level, dst);
                if (stack.isEmpty()) return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    /** Extract up to max of the filter item; filter==null takes any stack. */
    private static ItemStack extractFiltered(Container container, int max, Item filter) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty() && (filter == null || s.is(filter))) {
                return container.removeItem(i, Math.min(max, s.getCount()));
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public String status() {
        return super.status() + " filter=" + (filter == null ? "-" : BuiltInRegistries.ITEM.getKey(filter))
                + " last=" + lastAct;
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        if (filter != null) out.putString("filter", BuiltInRegistries.ITEM.getKey(filter).toString());
        if (!transit.isEmpty()) out.store("transit", ItemStack.CODEC, transit);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        String s = in.getStringOr("filter", "");
        filter = s.isEmpty() ? null : BuiltInRegistries.ITEM.getValue(Identifier.parse(s));
        transit = in.read("transit", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
