package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Gravitite ore (Aether analogue): the enchanted ore floats upward - every few
 * ticks the block moves one block up while the space above is air.
 */
public class GravititeBlock extends Block {

    public GravititeBlock(Properties props) {
        super(props);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState old,
            boolean movedByPiston) {
        super.onPlace(state, level, pos, old, movedByPiston);
        level.scheduleTick(pos, this, 4);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos above = pos.above();
        if (above.getY() <= level.getMaxY() && level.getBlockState(above).isAir()) {
            level.setBlock(above, state, Block.UPDATE_ALL);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }
}
