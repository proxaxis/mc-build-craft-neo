package com.buildcraftneo.industry;

import com.buildcraftneo.data.Profiles;
import com.buildcraftneo.economy.Mint;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

/**
 * ATM (Lightman's Currency analogue): right-click deposits all carried coins
 * into the MD wallet, sneak-right-click withdraws 500 MD as coins.
 */
public class AtmBlockEntity extends MachineBlockEntity {

    public AtmBlockEntity(BlockPos pos, BlockState state) {
        super(BCNBlockEntities.ATM, pos, state, 0);
    }

    public long deposit(ServerPlayer player) {
        return Mint.depositAll(player);
    }

    public long withdraw(ServerPlayer player, long amount) {
        return Mint.withdraw(player, amount);
    }

    @Override
    public String status() {
        return super.status() + " atm";
    }
}
