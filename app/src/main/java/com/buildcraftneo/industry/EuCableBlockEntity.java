package com.buildcraftneo.industry;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ticking endpoint for {@link EuCableBlock}. Every 10 ticks the cable with
 * the lowest position in a network (a deterministic single driver) moves EU
 * from the fullest adjacent {@link EuStorage} to the emptiest one, capped by
 * the network's slowest cable rate and taxed by its worst cable loss.
 */
public class EuCableBlockEntity extends BlockEntity {
    private static final int MAX_NODES = 256;

    public EuCableBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.EU_CABLE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state,
            EuCableBlockEntity self) {
        if (level.isClientSide() || level.getGameTime() % 10 != 0) return;

        // Map the connected cable network; only the lowest-position node
        // performs the transfer so it happens once per network per cycle.
        Set<BlockPos> net = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(pos);
        while (!queue.isEmpty() && net.size() < MAX_NODES) {
            BlockPos p = queue.poll();
            if (!net.add(p)) continue;
            for (Direction d : Direction.values()) {
                BlockPos np = p.relative(d);
                if (!net.contains(np)
                        && level.getBlockState(np).getBlock() instanceof EuCableBlock) {
                    queue.add(np);
                }
            }
        }
        BlockPos min = null;
        int rate = Integer.MAX_VALUE;
        int loss = 0;
        for (BlockPos p : net) {
            if (min == null || p.compareTo(min) < 0) min = p;
            if (level.getBlockState(p).getBlock() instanceof EuCableBlock c) {
                rate = Math.min(rate, c.transferRate);
                loss = Math.max(loss, c.loss);
            }
        }
        if (!pos.equals(min)) return;

        // Collect the EU stores touching the network.
        Set<EuStorage> stores = new HashSet<>();
        for (BlockPos p : net) {
            for (Direction d : Direction.values()) {
                if (level.getBlockEntity(p.relative(d)) instanceof EuStorage s) {
                    stores.add(s);
                }
            }
        }
        if (stores.size() < 2 || rate <= 0) return;

        EuStorage fullest = null, emptiest = null;
        for (EuStorage s : stores) {
            long eu = s.euStored();
            double fill = s.euCapacity() > 0 ? (double) eu / s.euCapacity() : 0;
            if (fullest == null || fill > (double) fullest.euStored()
                    / fullest.euCapacity()) fullest = s;
            if (emptiest == null || fill < (double) emptiest.euStored()
                    / emptiest.euCapacity()) emptiest = s;
        }
        long gap = fullest.euStored() - emptiest.euStored();
        long want = Math.min(rate, gap / 2);
        want = Math.min(want, emptiest.euCapacity() - emptiest.euStored());
        if (want <= 0) return;
        long moved = fullest.drainEu(want);
        if (moved <= 0) return;
        long delivered = emptiest.insertEu(Math.max(0, moved - loss));
        // anything the sink rejected flows back to the source
        if (delivered < moved - loss) {
            fullest.insertEu(moved - loss - delivered);
        }
    }
}
