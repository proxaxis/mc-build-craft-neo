package com.buildcraftneo.industry;

import com.buildcraftneo.BCN;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Dimensional gate: a walk-in portal block. Entities standing inside are
 * teleported to the target dimension's spawn (right-click cycles target:
 * twilight -> skyreach -> overworld). A short per-entity cooldown prevents
 * bounce-back loops between paired gates.
 */
public class DimensionGateBlockEntity extends MachineBlockEntity {
    private static final long COOLDOWN_TICKS = 100;
    private static final Map<java.util.UUID, Long> COOLDOWN = new ConcurrentHashMap<>();
    private static final Identifier[] TARGETS = {
            BCN.id("twilight"), BCN.id("skyreach"), Identifier.withDefaultNamespace("overworld"),
    };

    private int targetIdx = 0;

    public DimensionGateBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.DIMENSION_GATE, pos, state, 0);
    }

    public Identifier target() {
        return TARGETS[targetIdx % TARGETS.length];
    }

    public void cycleTarget() {
        targetIdx = (targetIdx + 1) % TARGETS.length;
        setChanged();
    }

    public void onEntityInside(Level level, Entity entity) {
        if (level.isClientSide() || !(entity instanceof LivingEntity)) return;
        long now = level.getGameTime();
        Long last = COOLDOWN.get(entity.getUUID());
        if (last != null && now - last < COOLDOWN_TICKS) return;

        ServerLevel dest = level.getServer().getLevel(
                ResourceKey.create(Registries.DIMENSION, target()));
        if (dest == null || dest == level) return;

        COOLDOWN.put(entity.getUUID(), now);
        BlockPos spawn = dest.getRespawnData().pos();
        entity.teleportTo(dest, spawn.getX() + 0.5, spawn.getY() + 1.0, spawn.getZ() + 0.5,
                java.util.Set.of(), entity.getYRot(), entity.getXRot(), true);
    }

    /** Ticker entry: prunes the cooldown map occasionally. */
    public static void tick(Level level, BlockPos pos, BlockState state, DimensionGateBlockEntity self) {
        if (!level.isClientSide() && level.getGameTime() % 4000 == 0) {
            COOLDOWN.entrySet().removeIf(e -> level.getGameTime() - e.getValue() > COOLDOWN_TICKS * 10);
        }
    }

    @Override
    public String status() {
        return super.status() + " gate-> " + target();
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("targetIdx", targetIdx);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        targetIdx = in.getIntOr("targetIdx", 0);
    }
}
