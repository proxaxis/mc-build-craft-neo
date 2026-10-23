package com.buildcraftneo.industry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
 * Channels are cross-dimensional. Right-click cycles channel (0-7),
 * sneak-right-click cycles mode (send+receive / send only / receive only).
 */
public class TeleportPipeBlockEntity extends MachineBlockEntity {
    public static final int MODE_BOTH = 0;
    public static final int MODE_SEND = 1;
    public static final int MODE_RECEIVE = 2;
    /** ticks during which a container that just received a teleport delivery is not pulled from */
    private static final long RECENT_WINDOW = 200;

    private record Ref(Identifier dim, BlockPos pos) {}

    /** channel -> pipe endpoints across all dimensions */
    private static final Map<Integer, Set<Ref>> NETWORK = new ConcurrentHashMap<>();
    /** containers that recently received a teleport delivery: ref -> gameTime */
    private static final Map<Ref, Long> RECENT = new ConcurrentHashMap<>();

    private int channel = 0;
    private int mode = MODE_BOTH;
    private ItemStack transit = ItemStack.EMPTY;
    private boolean registered;
    private String lastAct = "-";

    public TeleportPipeBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.TELEPORT_PIPE, pos, state, 0);
    }

    public int channel() {
        return channel;
    }

    public int mode() {
        return mode;
    }

    public void cycleChannel() {
        channel = (channel + 1) % 8;
        registered = false;
        setChanged();
    }

    public void cycleMode() {
        mode = (mode + 1) % 3;
        setChanged();
    }

    /** Called by a wired item pipe handing a stack to this pipe for remote delivery. */
    public ItemStack inject(ItemStack stack) {
        if (!transit.isEmpty()) return stack;
        transit = stack.copy();
        setChanged();
        return ItemStack.EMPTY;
    }

    private void register(Level level) {
        Ref self = new Ref(level.dimension().identifier(), getBlockPos());
        NETWORK.values().forEach(s -> s.remove(self));
        NETWORK.computeIfAbsent(channel, c -> ConcurrentHashMap.newKeySet()).add(self);
        registered = true;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TeleportPipeBlockEntity self) {
        if (level.isClientSide()) return;
        if (!self.registered) self.register(level);
        if (level.getGameTime() % 10 != 0) return;
        if (level.getGameTime() % 2000 == 0) {
            RECENT.entrySet().removeIf(e -> level.getGameTime() - e.getValue() > RECENT_WINDOW * 10);
        }

        if (!self.transit.isEmpty()) {
            self.transit = self.deliverRemote(level, self.transit);
            self.setChanged();
            return;
        }
        if (self.mode == MODE_RECEIVE) return;

        Identifier dim = level.dimension().identifier();
        for (Direction dir : Direction.values()) {
            BlockPos src = pos.relative(dir);
            BlockEntity be = level.getBlockEntity(src);
            if (!(be instanceof Container container) || be instanceof ItemPipeBlockEntity
                    || be instanceof TeleportPipeBlockEntity || be instanceof RequestPipeBlockEntity) {
                continue;
            }
            Long t = RECENT.get(new Ref(dim, src));
            if (t != null && level.getGameTime() - t < RECENT_WINDOW) continue;
            ItemStack taken = ItemPipeBlockEntity.extract(container, 8);
            if (!taken.isEmpty()) {
                self.lastAct = "pull " + taken.getCount() + "x <- " + dir;
                self.transit = self.deliverRemote(level, taken);
                self.setChanged();
                return;
            }
        }
    }

    /** Send the stack to a container adjacent to another same-channel pipe. */
    private ItemStack deliverRemote(Level level, ItemStack stack) {
        Identifier dim = level.dimension().identifier();
        var peers = NETWORK.getOrDefault(channel, Set.of());
        for (Ref peer : peers) {
            if (peer.dim().equals(dim) && peer.pos().equals(getBlockPos())) continue;
            ServerLevel pl = peer.dim().equals(dim) ? (ServerLevel) level
                    : level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, peer.dim()));
            if (pl == null) continue;
            BlockEntity pe = pl.getBlockEntity(peer.pos());
            if (!(pe instanceof TeleportPipeBlockEntity tp) || tp.mode == MODE_SEND) continue;
            for (Direction dir : Direction.values()) {
                BlockPos dst = peer.pos().relative(dir);
                BlockEntity be = pl.getBlockEntity(dst);
                if (be instanceof Container container && !(be instanceof MachineBlockEntity)) {
                    int before = stack.getCount();
                    stack = ItemPipeBlockEntity.insert(container, stack);
                    if (stack.getCount() != before) {
                        RECENT.put(new Ref(peer.dim(), dst), pl.getGameTime());
                        lastAct = "sent -> " + peer.dim() + " " + dst.toShortString();
                    }
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }
        lastAct = "no-outlet ch=" + channel + " peers=" + peers.size();
        return stack; // no remote outlet: keep in transit
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null) {
            Ref self = new Ref(level.dimension().identifier(), getBlockPos());
            NETWORK.values().forEach(s -> s.remove(self));
        }
    }

    private static String modeName(int mode) {
        return switch (mode) {
            case MODE_SEND -> "send";
            case MODE_RECEIVE -> "receive";
            default -> "both";
        };
    }

    @Override
    public String status() {
        return super.status() + " ch=" + channel + " mode=" + modeName(mode)
                + " peers=" + NETWORK.getOrDefault(channel, Set.of()).size()
                + " transit=" + (transit.isEmpty() ? "-" : transit.getCount() + "x")
                + " last=" + lastAct;
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("channel", channel);
        out.putInt("mode", mode);
        if (!transit.isEmpty()) out.store("transit", ItemStack.CODEC, transit);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        channel = in.getIntOr("channel", 0);
        mode = in.getIntOr("mode", MODE_BOTH);
        transit = in.read("transit", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        registered = false;
    }
}
