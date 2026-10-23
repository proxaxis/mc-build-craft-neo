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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Request pipe (LogisticsPipes analogue): on demand, pulls a requested item
 * from any container reachable through the connected pipe network and
 * deposits it into an adjacent non-pipe container (or drops at the pipe).
 */
public class RequestPipeBlockEntity extends MachineBlockEntity {
    public static final int MAX_HOPS = 64;

    private Item requested;
    private ItemStack transit = ItemStack.EMPTY;

    public RequestPipeBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.REQUEST_PIPE, pos, state, 0);
    }

    /** Set the requested item (via command/UI). */
    public void setRequested(Item item) {
        requested = item;
        setChanged();
    }

    public Item requested() {
        return requested;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RequestPipeBlockEntity self) {
        if (level.isClientSide()) return;
        if (!self.transit.isEmpty() && level.getGameTime() % 10 == 0) {
            self.transit = self.depositAdjacent(level, pos, self.transit);
            self.setChanged();
            return;
        }
        if (self.requested == null || level.getGameTime() % 20 != 0) return;

        ItemStack found = self.requestFromNetwork(level, pos);
        if (!found.isEmpty()) {
            self.transit = self.depositAdjacent(level, pos, found);
            self.setChanged();
        }
    }

    /** BFS the pipe network; extract up to a stack of the requested item from the first container holding it. */
    private ItemStack requestFromNetwork(Level level, BlockPos start) {
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
                if (be instanceof ItemPipeBlockEntity || be instanceof RequestPipeBlockEntity
                        || be instanceof TeleportPipeBlockEntity) {
                    queue.add(next);
                } else if (be instanceof Container container && !cur.equals(start)) {
                    // containers directly adjacent to the request pipe are outputs, never sources
                    ItemStack found = extractItem(container, requested);
                    if (!found.isEmpty()) return found;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack extractItem(Container container, Item item) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty() && s.is(item)) {
                return container.removeItem(i, Math.min(64, s.getCount()));
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack depositAdjacent(Level level, BlockPos pos, ItemStack stack) {
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (be instanceof Container container && !(be instanceof ItemPipeBlockEntity)
                    && !(be instanceof RequestPipeBlockEntity) && !(be instanceof TeleportPipeBlockEntity)) {
                stack = ItemPipeBlockEntity.insert(container, stack);
                if (stack.isEmpty()) return ItemStack.EMPTY;
            }
        }
        if (!stack.isEmpty()) {
            net.minecraft.world.level.block.Block.popResource(level, pos.above(), stack);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public String status() {
        return super.status() + " request=" + (requested == null ? "-" :
                net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(requested))
                + " transit=" + (transit.isEmpty() ? "-" : transit.getCount() + "x");
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        if (requested != null) {
            out.putString("request", net.minecraft.core.registries.BuiltInRegistries.ITEM
                    .getKey(requested).toString());
        }
        if (!transit.isEmpty()) out.store("transit", ItemStack.CODEC, transit);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        String r = in.getStringOr("request", "");
        requested = r.isEmpty() ? null : net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getValue(net.minecraft.resources.Identifier.parse(r));
        transit = in.read("transit", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
