package com.buildcraftneo.data;

import java.util.Locale;

public enum Faction {
    NONE,
    SCIENCE,
    MAGIC;

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Faction byName(String name) {
        if (name == null) return NONE;
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "science" -> SCIENCE;
            case "magic" -> MAGIC;
            default -> NONE;
        };
    }
}
