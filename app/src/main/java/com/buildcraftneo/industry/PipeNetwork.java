package com.buildcraftneo.industry;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Shared BFS helpers for the logistics pipe network (item/request/teleport/supplier/sorting/crafting). */
public final class PipeNetwork {
    private PipeNetwork() {}

    public static final int MAX_HOPS = 64;
    /** ticks during which a freshly-supplied container is not pulled from */
    private static final long DELIVERY_WINDOW = 200;

    private record Ref(net.minecraft.resources.Identifier dim, BlockPos pos) {}

    private static final java.util.Map<Ref, Long> DELIVERED = new java.util.concurrent.ConcurrentHashMap<>();

    /** Mark a container as having just received a network delivery. */
    public static void noteDelivery(Level level, BlockPos pos) {
        DELIVERED.put(new Ref(level.dimension().identifier(), pos.immutable()),
                level.getGameTime());
    }

    /** True while the container should not be used as a pull source. */
    public static boolean recentlySupplied(Level level, BlockPos pos) {
        Long t = DELIVERED.get(new Ref(level.dimension().identifier(), pos));
        return t != null && level.getGameTime() - t < DELIVERY_WINDOW;
    }

    /** Any block entity that transports items through the pipe network. */
    public static boolean isPipe(BlockEntity be) {
        return be instanceof ItemPipeBlockEntity || be instanceof RequestPipeBlockEntity
                || be instanceof TeleportPipeBlockEntity || be instanceof SupplierPipeBlockEntity
                || be instanceof SortingPipeBlockEntity || be instanceof CraftingPipeBlockEntity;
    }

    /** Wired pipes that forward items (teleport pipes are wireless endpoints, sorting pipes filter). */
    private static boolean isForwarder(BlockEntity be) {
        return be instanceof ItemPipeBlockEntity || be instanceof RequestPipeBlockEntity
                || be instanceof SupplierPipeBlockEntity || be instanceof CraftingPipeBlockEntity;
    }

    /**
     * Route a stack through the connected pipe network to the first reachable
     * destination container. Sorting pipes get first claim on matching items for
     * their adjacent containers; teleport pipes forward remotely.
     */
    public static ItemStack route(Level level, BlockPos start, ItemStack stack, BlockPos exclude) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        queue.add(start);
        seen.add(start);

        while (!queue.isEmpty() && seen.size() < MAX_HOPS) {
            BlockPos cur = queue.poll();
            boolean curIsSorter = level.getBlockEntity(cur) instanceof SortingPipeBlockEntity;
            for (Direction dir : Direction.values()) {
                BlockPos next = cur.relative(dir);
                if (!seen.add(next)) continue;
                BlockEntity be = level.getBlockEntity(next);
                if (be instanceof TeleportPipeBlockEntity tp) {
                    stack = tp.inject(stack);
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                } else if (be instanceof SortingPipeBlockEntity sp) {
                    stack = sp.tryRoute(stack);
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                    queue.add(next);
                } else if (isForwarder(be)) {
                    queue.add(next);
                } else if (be instanceof Container container && !next.equals(exclude) && !curIsSorter) {
                    int before = stack.getCount();
                    stack = ItemPipeBlockEntity.insert(container, stack);
                    if (stack.getCount() != before) noteDelivery(level, next);
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }
        return stack; // undeliverable: caller keeps it in transit
    }

    /**
     * BFS the network and extract up to {@code max} of {@code item} from the
     * first reachable containers holding it. Containers directly adjacent to
     * {@code start} are treated as outputs and are never used as sources.
     */
    public static ItemStack pullFromNetwork(Level level, BlockPos start, Item item, int max) {
        ItemStack result = ItemStack.EMPTY;
        int remaining = max;
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        queue.add(start);
        seen.add(start);

        while (!queue.isEmpty() && seen.size() < MAX_HOPS && remaining > 0) {
            BlockPos cur = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos next = cur.relative(dir);
                if (!seen.add(next)) continue;
                BlockEntity be = level.getBlockEntity(next);
                if (isPipe(be)) {
                    queue.add(next);
                } else if (be instanceof Container container && !cur.equals(start)) {
                    ItemStack got = extractItem(container, item, remaining);
                    if (!got.isEmpty()) {
                        remaining -= got.getCount();
                        if (result.isEmpty()) result = got;
                        else result.grow(got.getCount());
                    }
                    if (remaining <= 0) return result;
                }
            }
        }
        return result;
    }

    /** Total count of {@code item} reachable through the network (excluding containers adjacent to start). */
    public static int countInNetwork(Level level, BlockPos start, Item item) {
        int total = 0;
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
                if (isPipe(be)) {
                    queue.add(next);
                } else if (be instanceof Container container && !cur.equals(start)) {
                    total += countIn(container, item);
                }
            }
        }
        return total;
    }

    /** Insert into the first adjacent non-pipe container; drops leftovers above the pipe. */
    public static ItemStack depositAdjacent(Level level, BlockPos pos, ItemStack stack) {
        for (Direction dir : Direction.values()) {
            BlockPos dst = pos.relative(dir);
            BlockEntity be = level.getBlockEntity(dst);
            if (be instanceof Container container && !isPipe(be)) {
                int before = stack.getCount();
                stack = ItemPipeBlockEntity.insert(container, stack);
                if (stack.getCount() != before) noteDelivery(level, dst);
                if (stack.isEmpty()) return ItemStack.EMPTY;
            }
        }
        if (!stack.isEmpty()) {
            Block.popResource(level, pos.above(), stack);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    private static ItemStack extractItem(Container container, Item item, int max) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty() && s.is(item)) {
                return container.removeItem(i, Math.min(max, s.getCount()));
            }
        }
        return ItemStack.EMPTY;
    }

    private static int countIn(Container container, Item item) {
        int n = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty() && s.is(item)) n += s.getCount();
        }
        return n;
    }
}
