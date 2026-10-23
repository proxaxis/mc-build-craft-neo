package com.buildcraftneo.industry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Teleport pipe (AdditionalPipes analogue): wirelessly transfers pulled items
 * to a container adjacent to another teleport pipe on the same channel.
 * Channel is a public int shared per-dimension (cycle with right-click / data merge).
 */
public class TeleportPipeBlockEntity extends MachineBlockEntity {
    /** dimension id -> channel -> pipe positions */
    private static final Map<Identifier, Map<Integer, Set<BlockPos>>> NETWORK = new ConcurrentHashMap<>();

    private int channel = 0;
    private ItemStack transit = ItemStack.EMPTY;
    private boolean registered;

    public TeleportPipeBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.TELEPORT_PIPE, pos, state, 0);
    }

    public int channel() {
        return channel;
    }

    public void cycleChannel() {
        channel = (channel + 1) % 8;
        registered = false;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TeleportPipeBlockEntity self) {
        if (level.isClientSide()) return;
        if (!self.registered) {
            NETWORK.computeIfAbsent(level.dimension().identifier(), d -> new ConcurrentHashMap<>())
                    .computeIfAbsent(self.channel, c -> ConcurrentHashMap.newKeySet())
                    .add(pos);
            self.registered = true;
        }
        if (level.getGameTime() % 10 != 0) return;

        if (!self.transit.isEmpty()) {
            self.transit = self.deliverRemote(level, self.transit);
            self.setChanged();
            return;
        }
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (be instanceof Container container && !(be instanceof ItemPipeBlockEntity)
                    && !(be instanceof TeleportPipeBlockEntity)) {
                ItemStack taken = ItemPipeBlockEntity.extract(container, 8);
                if (!taken.isEmpty()) {
                    self.transit = self.deliverRemote(level, taken);
                    self.setChanged();
                    return;
                }
            }
        }
    }

    /** Send the stack to a container adjacent to another same-channel pipe. */
    private ItemStack deliverRemote(Level level, ItemStack stack) {
        var byChannel = NETWORK.getOrDefault(level.dimension().identifier(), Map.of());
        var peers = byChannel.getOrDefault(channel, Set.of());
        for (BlockPos peer : peers) {
            if (peer.equals(getBlockPos())) continue;
            for (Direction dir : Direction.values()) {
                BlockEntity be = level.getBlockEntity(peer.relative(dir));
                if (be instanceof Container container && !(be instanceof MachineBlockEntity)) {
                    stack = ItemPipeBlockEntity.insert(container, stack);
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }
        return stack; // no remote outlet: keep in transit
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        var byChannel = NETWORK.get(level.dimension().identifier());
        if (byChannel != null) {
            byChannel.values().forEach(s -> s.remove(getBlockPos()));
        }
    }

    @Override
    public String status() {
        return super.status() + " ch=" + channel
                + " transit=" + (transit.isEmpty() ? "-" : transit.getCount() + "x");
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("channel", channel);
        if (!transit.isEmpty()) out.store("transit", ItemStack.CODEC, transit);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        channel = in.getIntOr("channel", 0);
        transit = in.read("transit", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
