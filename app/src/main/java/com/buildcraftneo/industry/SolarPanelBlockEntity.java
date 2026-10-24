package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * IC2-style solar panel approximation: generates 1 MJ/t while the block has
 * direct sunlight (daytime, sky visible, no rain) and pushes it to adjacent
 * machines. No fuel slot - it needs nothing but light.
 */
public class SolarPanelBlockEntity extends MachineBlockEntity {
    public static final int MJ_PER_TICK = 1;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.SOLAR_PANEL, pos, state, 0);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity self) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
        if (level.canSeeSky(pos.above()) && level.isBrightOutside() && !level.isRaining()) {
            self.mj = Math.min(self.mjCapacity(), self.mj + MJ_PER_TICK);
        }
        if (self.mj > 0) {
            long moved = Mj.pushToNeighbors(level, pos, self.mj);
            if (moved > 0) {
                self.mj -= moved;
                com.buildcraftneo.data.Profiles.addMjGenerated(serverLevel, moved);
            }
        }
    }

    @Override
    public int progressPercent() {
        return 0;
    }
}
