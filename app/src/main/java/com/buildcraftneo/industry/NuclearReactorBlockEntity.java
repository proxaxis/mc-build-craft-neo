package com.buildcraftneo.industry;

import com.buildcraftneo.data.Profiles;
import com.buildcraftneo.item.BCNItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Nuclear reactor (IC2 analogue): burns uranium ingots for 100 MJ/t.
 * Heat builds while running; adjacent water cools it. Heat >= 100 -> meltdown.
 */
public class NuclearReactorBlockEntity extends MachineBlockEntity {
    public static final int MJ_PER_TICK = 100;
    public static final int FUEL_TICKS = 1600;
    public static final double HEAT_PER_TICK = 0.05;
    public static final double COOLING_PER_TICK = 0.1;
    public static final double MELTDOWN_HEAT = 100.0;

    private int fuelLeft;
    private double heat;

    public NuclearReactorBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.NUCLEAR_REACTOR, pos, state, 1);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, NuclearReactorBlockEntity self) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;

        if (self.fuelLeft <= 0) {
            ItemStack fuel = self.items.get(0);
            if (!fuel.isEmpty() && fuel.is(BCNItems.URANIUM_INGOT)) {
                fuel.shrink(1);
                self.fuelLeft = FUEL_TICKS;
                self.setChanged();
            }
        }

        boolean running = self.fuelLeft > 0;
        if (running) {
            self.fuelLeft--;
            self.mj = Math.min(self.mjCapacity(), self.mj + MJ_PER_TICK);
            self.heat += HEAT_PER_TICK;
        }
        // water cooling
        if (isAdjacentWater(level, pos)) {
            self.heat = Math.max(0, self.heat - COOLING_PER_TICK);
        }
        // meltdown check (cheap: every 10 ticks)
        if (self.heat >= MELTDOWN_HEAT && serverLevel.getGameTime() % 10 == 0) {
            meltDown(serverLevel, pos, self);
            return;
        }
        if (self.mj > 0) {
            long moved = Mj.pushToNeighbors(level, pos, self.mj);
            if (moved > 0) {
                self.mj -= moved;
                Profiles.addMjGenerated(serverLevel, moved);
            }
        }
        if (running && serverLevel.getGameTime() % 100 == 0) {
            Profiles.addEfp(serverLevel.getServer().overworld(), 0.2);
        }
    }

    private static boolean isAdjacentWater(Level level, BlockPos pos) {
        for (Direction d : Direction.values()) {
            if (level.getBlockState(pos.relative(d)).is(Blocks.WATER)) return true;
        }
        return false;
    }

    private static void meltDown(ServerLevel level, BlockPos pos, NuclearReactorBlockEntity self) {
        level.destroyBlock(pos, false);
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10.0f, true, Level.ExplosionInteraction.BLOCK);
        Profiles.addEfp(level.getServer().overworld(), 500.0);
    }

    public double heat() {
        return heat;
    }

    @Override
    public String status() {
        return super.status() + String.format(" fuel=%dt heat=%.1f", fuelLeft, heat);
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("fuelLeft", fuelLeft);
        out.putDouble("heat", heat);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        fuelLeft = in.getIntOr("fuelLeft", 0);
        heat = in.getDoubleOr("heat", 0.0);
    }
}
