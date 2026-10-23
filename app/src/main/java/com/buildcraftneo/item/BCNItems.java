package com.buildcraftneo.item;

import com.buildcraftneo.BCN;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class BCNItems {
    private BCNItems() {}

    // currency (MD) coin tiers, copper < iron < gold < emerald < diamond < netherite
    public static Item COPPER_COIN;
    public static Item IRON_COIN;
    public static Item GOLD_COIN;
    public static Item EMERALD_COIN;
    public static Item DIAMOND_COIN;
    public static Item NETHERITE_COIN;

    // industry tokens
    public static Item WRENCH;
    public static Item CIRCUIT;
    public static Item URANIUM_INGOT;

    // macerator outputs (smelt -> ingot)
    public static Item IRON_DUST;
    public static Item GOLD_DUST;
    public static Item COPPER_DUST;

    // magic tokens
    public static Item APPRENTICE_WAND;

    // science weapons
    public static Item SERVICE_PISTOL;

    // job ability items
    public static Item ABILITY_WARCRY;
    public static Item ABILITY_TAUNT;
    public static Item ABILITY_SHADOWSTEP;
    public static Item ABILITY_SANCTUARY;
    public static Item ABILITY_METEOR;
    public static Item ABILITY_FROST_NOVA;
    public static Item ABILITY_RAIN_OF_ARROWS;
    public static Item ABILITY_PIERCE_SHOT;

    /** MD value of each coin tier. */
    public static final java.util.Map<Item, Long> COIN_VALUES = new java.util.LinkedHashMap<>();

    public static void register() {
        COPPER_COIN = simple("copper_coin");
        IRON_COIN = simple("iron_coin");
        GOLD_COIN = simple("gold_coin");
        EMERALD_COIN = simple("emerald_coin");
        DIAMOND_COIN = simple("diamond_coin");
        NETHERITE_COIN = simple("netherite_coin");
        WRENCH = simple("wrench");
        CIRCUIT = simple("circuit");
        URANIUM_INGOT = simple("uranium_ingot");
        IRON_DUST = simple("iron_dust");
        GOLD_DUST = simple("gold_dust");
        COPPER_DUST = simple("copper_dust");
        APPRENTICE_WAND = spell("apprentice_wand", new Item.Properties());
        SERVICE_PISTOL = gun("service_pistol", 8.0, 8);
        ABILITY_WARCRY = ability("ability_warcry", com.buildcraftneo.data.Job.WARRIOR, 20, 200, Abilities::warcry);
        ABILITY_TAUNT = ability("ability_taunt", com.buildcraftneo.data.Job.GUARDIAN, 15, 200, Abilities::taunt);
        ABILITY_SHADOWSTEP = ability("ability_shadowstep", com.buildcraftneo.data.Job.ASSASSIN, 15, 100, Abilities::shadowstep);
        ABILITY_SANCTUARY = ability("ability_sanctuary", com.buildcraftneo.data.Job.HEALER, 25, 200, Abilities::sanctuary);
        ABILITY_METEOR = ability("ability_meteor", com.buildcraftneo.data.Job.MAGE, 40, 300, Abilities::meteor);
        ABILITY_FROST_NOVA = ability("ability_frost_nova", com.buildcraftneo.data.Job.MAGE, 30, 300, Abilities::frostNova);
        ABILITY_RAIN_OF_ARROWS = ability("ability_rain_of_arrows", com.buildcraftneo.data.Job.ARCHER, 30, 300, Abilities::rainOfArrows);
        ABILITY_PIERCE_SHOT = ability("ability_pierce_shot", com.buildcraftneo.data.Job.ARCHER, 15, 100, Abilities::pierceShot);

        COIN_VALUES.put(COPPER_COIN, 1L);
        COIN_VALUES.put(IRON_COIN, 10L);
        COIN_VALUES.put(GOLD_COIN, 100L);
        COIN_VALUES.put(EMERALD_COIN, 1000L);
        COIN_VALUES.put(DIAMOND_COIN, 10000L);
        COIN_VALUES.put(NETHERITE_COIN, 100000L);
    }

    private static Item simple(String name) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id, new Item(new Item.Properties().setId(key)));
    }

    private static Item spell(String name, Item.Properties props) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id, new FireballWandItem(props.setId(key)));
    }

    private static Item gun(String name, double damage, int cooldownTicks) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new GunItem(new Item.Properties().setId(key), damage, cooldownTicks));
    }

    private static Item ability(String name, com.buildcraftneo.data.Job job, int mpCost,
            int cooldown, java.util.function.BiConsumer<net.minecraft.server.level.ServerPlayer,
                    net.minecraft.server.level.ServerLevel> effect) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new AbilityItem(new Item.Properties().setId(key), job, mpCost, cooldown, effect));
    }
}
