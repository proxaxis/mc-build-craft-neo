package com.buildcraftneo.data;

import java.util.Locale;

public enum Job {
    NONE,
    WARRIOR,
    ASSASSIN,
    GUARDIAN,
    MAGE,
    HEALER,
    ARCHER,
    SCIENTIST;

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Job byName(String name) {
        if (name == null) return NONE;
        try {
            return Job.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }

    public boolean isAllowedFor(Faction faction) {
        return switch (faction) {
            case SCIENCE -> this == SCIENTIST || this == NONE;
            case MAGIC, NONE -> this != SCIENTIST;
        };
    }
}
