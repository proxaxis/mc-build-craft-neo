package com.buildcraftneo;

import net.minecraft.resources.Identifier;

public final class BCN {
    public static final String MOD_ID = "buildcraftneo";

    private BCN() {}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
