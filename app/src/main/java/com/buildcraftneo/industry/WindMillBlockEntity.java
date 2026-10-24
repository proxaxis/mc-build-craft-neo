package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * IC2-style wind mill approximation: generates MJ while it can see the sky,
 * scaling with altitude (0 MJ/t below Y=64, +1 MJ/t per 16 blocks above,
 * capped at 4 MJ/t - storm weather adds a gust bonus). Pushes output to
 * adjacent machines like the solar panel.
 */
public class WindMillBlockEntity extends MachineBlockEntity {
    public static final int MAX_MJ_PER_TICK = 4;

    public WindMillBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.WIND_MILL, pos, state, 0);
    }

    public static int outputFor(Level level, BlockPos pos) {
        if (!level.canSeeSky(pos.above())) return 0;
        int base = Math.clamp((pos.getY() - 64) / 16 + 1, 0, MAX_MJ_PER_TICK);
        if (level.isThundering()) base = Math.min(MAX_MJ_PER_TICK + 1, base + 1);
        return base;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WindMillBlockEntity self) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
        self.mj = Math.min(self.mjCapacity(), self.mj + outputFor(level, pos));
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
        Level level = getLevel();
        return level == null ? 0 : outputFor(level, getBlockPos()) * 100 / (MAX_MJ_PER_TICK + 1);
    }

    @Override
    public String status() {
        Level level = getLevel();
        return super.status() + " gen="
                + (level == null ? 0 : outputFor(level, getBlockPos())) + "MJ/t";
    }
}
