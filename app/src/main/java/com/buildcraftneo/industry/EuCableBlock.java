package com.buildcraftneo.industry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * IC2-style EU cable approximation: cables form a network that periodically
 * equalises the charge of every {@link EuStorage} machine touching it
 * (bat_box, charge_pad). Each cable tier contributes a throughput and a
 * flat loss per transfer; a mixed network runs at the slowest cable's rate
 * and pays the worst cable's loss.
 */
public class EuCableBlock extends Block implements EntityBlock {
    /** EU moved between the fullest and emptiest endpoint per transfer (10t). */
    public final int transferRate;
    /** EU lost on each transfer event, approximating per-length cable loss. */
    public final int loss;

    public EuCableBlock(Properties properties, int transferRate, int loss) {
        super(properties);
        this.transferRate = transferRate;
        this.loss = loss;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EuCableBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null
                : (BlockEntityTicker<T>) (BlockEntityTicker<EuCableBlockEntity>)
                        EuCableBlockEntity::tick;
    }
}
