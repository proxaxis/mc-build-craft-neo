package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * IC2-style geothermal generator approximation: each adjacent lava source
 * block feeds the generator 2 MJ/t (up to 8 MJ/t with four exposed faces).
 * The lava is not consumed - a real IC2 setup burns lava cells, which is
 * approximated by requiring stationary lava plumbing instead.
 */
public class GeothermalGeneratorBlockEntity extends MachineBlockEntity {
    public static final int MJ_PER_LAVA = 2;

    public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.GEOTHERMAL_GENERATOR, pos, state, 0);
    }

    public static int lavaSources(Level level, BlockPos pos) {
        int n = 0;
        for (Direction d : Direction.values()) {
            var st = level.getBlockState(pos.relative(d));
            if (st.getFluidState().is(FluidTags.LAVA)
                    && st.getFluidState().isSource()) {
                n++;
            }
        }
        return n;
    }

    public static void tick(Level level, BlockPos pos, BlockState state,
            GeothermalGeneratorBlockEntity self) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
        int gen = lavaSources(level, pos) * MJ_PER_LAVA;
        if (gen > 0) {
            self.mj = Math.min(self.mjCapacity(), self.mj + gen);
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
        Level level = getLevel();
        return level == null ? 0 : lavaSources(level, getBlockPos()) * 100 / 6;
    }

    @Override
    public String status() {
        Level level = getLevel();
        return super.status() + " lava="
                + (level == null ? 0 : lavaSources(level, getBlockPos()));
    }
}
