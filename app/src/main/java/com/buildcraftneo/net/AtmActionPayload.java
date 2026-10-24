package com.buildcraftneo.net;

import com.buildcraftneo.BCN;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client→server: a button press inside the ATM screen. The server resolves
 * the player's currently open {@code AtmMenu} and validates it is still in
 * range, so no position needs to round-trip.
 */
public record AtmActionPayload(int action) implements CustomPacketPayload {
    /** Deposit every coin in the inventory. */
    public static final int DEPOSIT_ALL = 0;
    /** Withdraw 500 MD as coins. */
    public static final int WITHDRAW_500 = 1;

    public static final Type<AtmActionPayload> TYPE =
            new Type<>(BCN.id("atm_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AtmActionPayload> CODEC =
            StreamCodec.of((buf, p) -> buf.writeVarInt(p.action),
                    buf -> new AtmActionPayload(buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
