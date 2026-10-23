package com.buildcraftneo.industry;

import com.buildcraftneo.data.Profiles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Stirling engine: burns solid fuel (coal/charcoal/blaze rod) from its slot,
 * produces 4 MJ/t and pushes it to adjacent machines.
 */
public class StirlingEngineBlockEntity extends MachineBlockEntity {
    public static final int MJ_PER_TICK = 4;
    public static final int FUEL_TICKS = 1600;

    private int fuelLeft;
    private int fuelTotal;

    public StirlingEngineBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.STIRLING_ENGINE, pos, state, 1);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StirlingEngineBlockEntity self) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
        if (self.fuelLeft <= 0) {
            ItemStack fuel = self.items.get(0);
            if (!fuel.isEmpty() && isFuel(fuel)) {
                fuel.shrink(1);
                self.fuelLeft = FUEL_TICKS;
                self.fuelTotal = FUEL_TICKS;
                self.setChanged();
                // burning fuel pollutes a little
                Profiles.addEfp(serverLevel.getServer().overworld(), 0.5);
            }
        }
        if (self.fuelLeft > 0) {
            self.fuelLeft--;
            self.mj = Math.min(self.mjCapacity(), self.mj + MJ_PER_TICK);
        }
        if (self.mj > 0) {
            long moved = Mj.pushToNeighbors(level, pos, self.mj);
            if (moved > 0) {
                self.mj -= moved;
                Profiles.addMjGenerated(serverLevel, moved);
            }
        }
    }

    private static boolean isFuel(ItemStack stack) {
        return stack.is(Items.COAL) || stack.is(Items.CHARCOAL) || stack.is(Items.BLAZE_ROD);
    }

    public double fuelFraction() {
        return fuelTotal == 0 ? 0 : (double) fuelLeft / fuelTotal;
    }

    @Override
    public String status() {
        return super.status() + " fuel=" + fuelLeft + "t";
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("fuelLeft", fuelLeft);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        fuelLeft = in.getIntOr("fuelLeft", 0);
        fuelTotal = FUEL_TICKS;
    }
}
