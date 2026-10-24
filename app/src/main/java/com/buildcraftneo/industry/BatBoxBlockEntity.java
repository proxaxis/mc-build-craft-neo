package com.buildcraftneo.industry;

import com.buildcraftneo.item.EuChargeable;
import com.buildcraftneo.item.MiningLaserItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * IC2-style BatBox approximation: a tier-1 energy store. Generators push MJ
 * into it like any other consumer (we treat stored MJ as EU 1:1), and any
 * chargeable item placed into its two slots is recharged at up to 200 EU/t.
 */
public class BatBoxBlockEntity extends MachineBlockEntity implements EuStorage {
    public static final int EU_CAPACITY = 40_000;
    public static final int CHARGE_RATE = 200;

    public BatBoxBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.BAT_BOX, pos, state, 2);
    }

    @Override
    public long mjCapacity() {
        return EU_CAPACITY;
    }

    // ---- EuStorage (cable network endpoint) ----

    @Override
    public long euStored() {
        return mj;
    }

    @Override
    public long euCapacity() {
        return EU_CAPACITY;
    }

    @Override
    public long insertEu(long amount) {
        return amount - accept(amount);
    }

    @Override
    public long drainEu(long amount) {
        long taken = Math.min(Math.max(0, amount), mj);
        if (taken > 0) {
            mj -= taken;
            setChanged();
        }
        return taken;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BatBoxBlockEntity self) {
        if (level.isClientSide() || !(level instanceof ServerLevel)) return;
        for (int i = 0; i < self.items.size() && self.mj > 0; i++) {
            ItemStack s = self.items.get(i);
            if (s.getItem() instanceof EuChargeable tool) {
                long moved = tool.chargeEu(s, Math.min(CHARGE_RATE, self.mj));
                if (moved > 0) {
                    self.mj -= moved;
                    self.setChanged();
                }
            }
        }
    }

    @Override
    public String status() {
        StringBuilder sb = new StringBuilder(super.status()).append(" eu=").append(mj);
        for (int i = 0; i < items.size(); i++) {
            ItemStack s = items.get(i);
            if (s.getItem() instanceof EuChargeable tool) {
                sb.append(" slot").append(i).append(".charge=").append(tool.eu(s));
            }
        }
        return sb.toString();
    }
}
