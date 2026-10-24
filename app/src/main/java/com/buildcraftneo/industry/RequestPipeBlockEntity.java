package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
            self.transit = PipeNetwork.depositAdjacent(level, pos, found);
            self.setChanged();
        }
    }

    /** BFS the pipe network; extract up to a stack of the requested item from the first container holding it. */
    private ItemStack requestFromNetwork(Level level, BlockPos start) {
        return PipeNetwork.pullFromNetwork(level, start, requested, 64);
    }

    private ItemStack depositAdjacent(Level level, BlockPos pos, ItemStack stack) {
        return PipeNetwork.depositAdjacent(level, pos, stack);
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
