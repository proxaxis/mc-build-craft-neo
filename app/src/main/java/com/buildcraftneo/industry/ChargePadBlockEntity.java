package com.buildcraftneo.industry;

import com.buildcraftneo.item.EuChargeable;
import com.buildcraftneo.test.TestFakes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * IC2-style charge pad approximation: an MJ-fed energy store (treated as EU
 * 1:1, like the bat_box) that recharges any {@link EuChargeable} item held or
 * worn by a creature standing on top of it, at up to {@value #CHARGE_RATE}
 * EU/t per item.
 *
 * FakePlayers are not part of the level entity list, so the tick also scans
 * {@link TestFakes#all()} to keep the block verifiable from RCON.
 */
public class ChargePadBlockEntity extends MachineBlockEntity {
    public static final int EU_CAPACITY = 40_000;
    public static final int CHARGE_RATE = 200;
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public ChargePadBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.CHARGE_PAD, pos, state, 0);
    }

    @Override
    public long mjCapacity() {
        return EU_CAPACITY;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ChargePadBlockEntity self) {
        if (!(level instanceof ServerLevel sl) || self.mj <= 0) return;
        List<LivingEntity> targets = new ArrayList<>(sl.getEntitiesOfClass(
                LivingEntity.class, new AABB(pos.above()),
                e -> e.isAlive() && e.blockPosition().equals(pos.above())));
        for (var fake : TestFakes.all()) {
            if (fake.isAlive() && fake.level() == level
                    && fake.blockPosition().equals(pos.above())) {
                targets.add(fake);
            }
        }
        for (LivingEntity e : targets) {
            for (EquipmentSlot slot : SLOTS) {
                ItemStack s = e.getItemBySlot(slot);
                if (s.getItem() instanceof EuChargeable tool) {
                    long moved = tool.chargeEu(s, Math.min(CHARGE_RATE, self.mj));
                    if (moved > 0) {
                        self.mj -= moved;
                        self.setChanged();
                    }
                }
            }
        }
    }

    @Override
    public String status() {
        return super.status() + " eu=" + mj;
    }
}
