package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * ProjectRed-style timer gate: emits a short redstone pulse every
 * {@value #PERIOD} ticks. Output state is exposed via the {@code powered}
 * blockstate property.
 */
public class TimerBlockEntity extends BlockEntity {
    public static final int PERIOD = 40;   // 2 s cycle
    public static final int PULSE = 4;     // output-on ticks

    private int ticks;

    public TimerBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.TIMER, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TimerBlockEntity be) {
        if (level.isClientSide()) return;
        be.ticks++;
        if (be.ticks >= PERIOD) be.ticks = 0;
        boolean powered = be.ticks < PULSE;
        if (state.getValue(BlockStateProperties.POWERED) != powered) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, powered),
                    Block.UPDATE_ALL);
        }
    }
}
