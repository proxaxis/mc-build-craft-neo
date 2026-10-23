package com.buildcraftneo.test;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** Registry of FakePlayers spawned for RCON-driven testing. */
public final class TestFakes {
    private TestFakes() {}

    private static final Map<String, FakePlayer> FAKES = new HashMap<>();

    public static ServerPlayer spawn(ServerLevel level, String name) {
        FakePlayer fake = FakePlayer.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        FAKES.put(name, fake);
        return fake;
    }

    public static ServerPlayer get(String name) {
        return FAKES.get(name);
    }

    public static boolean remove(String name) {
        return FAKES.remove(name) != null;
    }

    public static String list() {
        return FAKES.isEmpty() ? "(none)" : String.join(", ", FAKES.keySet());
    }
}
