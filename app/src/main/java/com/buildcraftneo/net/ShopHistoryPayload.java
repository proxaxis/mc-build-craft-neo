package com.buildcraftneo.net;

import com.buildcraftneo.BCN;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server→client: current sale-history lines of an open shop menu. */
public record ShopHistoryPayload(BlockPos pos, List<String> lines)
        implements CustomPacketPayload {
    public static final Type<ShopHistoryPayload> TYPE =
            new Type<>(BCN.id("shop_history"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopHistoryPayload> CODEC =
            StreamCodec.of((buf, p) -> {
                buf.writeBlockPos(p.pos);
                buf.writeVarInt(p.lines.size());
                for (String s : p.lines) buf.writeUtf(s);
            }, buf -> {
                BlockPos pos = buf.readBlockPos();
                int n = buf.readVarInt();
                List<String> lines = new ArrayList<>(n);
                for (int i = 0; i < n; i++) lines.add(buf.readUtf());
                return new ShopHistoryPayload(pos, lines);
            });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
