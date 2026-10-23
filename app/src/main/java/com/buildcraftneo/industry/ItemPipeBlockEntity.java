package com.buildcraftneo.industry;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Item pipe: every 10 ticks pulls a stack from an adjacent non-pipe container
 * and routes it through connected pipes to the nearest reachable destination
 * container (chest, machine, ...).
 */
public class ItemPipeBlockEntity extends MachineBlockEntity {
    public static final int PERIOD = 10;
    public static final int BATCH = 8;
    public static final int MAX_HOPS = 32;

    private ItemStack transit = ItemStack.EMPTY;
    private BlockPos lastSource;
    private long ticks;
    private long pulls;
    private String lastNeighbor = "-";

    public ItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.ITEM_PIPE, pos, state, 0);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ItemPipeBlockEntity self) {
        self.ticks++;
        if (level.isClientSide() || level.getGameTime() % PERIOD != 0) return;

        if (!self.transit.isEmpty()) {
            self.transit = self.deliver(level, pos, self.transit, self.lastSource);
            self.setChanged();
            return;
        }

        // pull one stack from an adjacent non-pipe container
        self.pulls++;
        for (Direction dir : Direction.values()) {
            BlockPos sourcePos = pos.relative(dir);
            BlockEntity be = level.getBlockEntity(sourcePos);
            self.lastNeighbor = dir + " -> " + (be == null ? "null" : be.getClass().getSimpleName()
                    + " container=" + (be instanceof Container));
            if (be instanceof Container container && !(be instanceof ItemPipeBlockEntity)) {
                ItemStack taken = extract(container, BATCH);
                self.lastNeighbor += " size=" + container.getContainerSize()
                        + " s0=" + (container.getContainerSize() > 0
                            ? container.getItem(0).getCount() + "x"
                                + net.minecraft.core.registries.BuiltInRegistries.ITEM
                                    .getKey(container.getItem(0).getItem()) : "?")
                        + " taken=" + taken.getCount();
                if (!taken.isEmpty()) {
                    self.lastSource = sourcePos;
                    self.transit = self.deliver(level, pos, taken, sourcePos);
                    self.setChanged();
                    return;
                }
            }
        }
    }

    /** Route stack through pipe network to the first reachable destination container. */
    private ItemStack deliver(Level level, BlockPos start, ItemStack stack, BlockPos exclude) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        queue.add(start);
        seen.add(start);

        while (!queue.isEmpty() && seen.size() < MAX_HOPS) {
            BlockPos cur = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos next = cur.relative(dir);
                if (!seen.add(next)) continue;
                BlockEntity be = level.getBlockEntity(next);
                if (be instanceof ItemPipeBlockEntity || be instanceof RequestPipeBlockEntity) {
                    queue.add(next);
                } else if (be instanceof TeleportPipeBlockEntity tp) {
                    stack = tp.inject(stack);
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                } else if (be instanceof Container container && !next.equals(exclude)) {
                    stack = insert(container, stack);
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }
        return stack; // undeliverable: keep in transit, retry next tick
    }

    public static ItemStack extract(Container container, int max) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty()) {
                return container.removeItem(i, Math.min(max, s.getCount()));
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack insert(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize() && !stack.isEmpty(); i++) {
            ItemStack cur = container.getItem(i);
            if (cur.isEmpty()) {
                container.setItem(i, stack.copy());
                return ItemStack.EMPTY;
            }
            if (ItemStack.isSameItemSameComponents(cur, stack)) {
                int room = Math.min(cur.getMaxStackSize(), container.getMaxStackSize()) - cur.getCount();
                int move = Math.min(room, stack.getCount());
                if (move > 0) {
                    cur.grow(move);
                    stack.shrink(move);
                    container.setChanged();
                }
            }
        }
        return stack;
    }

    @Override
    public String status() {
        return super.status() + " ticks=" + ticks + " pulls=" + pulls + " last=" + lastNeighbor
                + " transit=" + (transit.isEmpty() ? "-" : transit.getCount() + "x"
                + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(transit.getItem()));
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        if (!transit.isEmpty()) {
            out.store("transit", ItemStack.CODEC, transit);
        }
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        transit = in.read("transit", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
