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
    public static Item ASSAULT_RIFLE;
    public static Item SNIPER_RIFLE;
    public static Item LMG;

    // melee weapons (Simply Swords analogue)
    public static Item KATANA;
    public static Item GREATSWORD;
    public static Item SPEAR;
    public static Item GLAIVE;
    public static Item RAPIER;
    public static Item CLAYMORE;
    public static Item WAR_DAGGER;
    public static Item SCYTHE;

    // dimension resources (Aether analogue)
    public static Item AMBROSIUM_SHARD;

    // tiered shields (Shield Expansion analogue)
    public static Item SHIELD_WOODEN;
    public static Item SHIELD_IRON;
    public static Item SHIELD_GOLDEN;
    public static Item SHIELD_DIAMOND;
    public static Item SHIELD_NETHERITE;

    // job ability items
    public static Item ABILITY_WARCRY;
    public static Item ABILITY_TAUNT;
    public static Item ABILITY_SHADOWSTEP;
    public static Item ABILITY_SANCTUARY;
    public static Item ABILITY_METEOR;
    public static Item ABILITY_FROST_NOVA;
    public static Item ABILITY_RAIN_OF_ARROWS;
    public static Item ABILITY_PIERCE_SHOT;

    // spell items (Iron's Spells analogue)
    public static Item ABILITY_HEAL;
    public static Item ABILITY_LIGHTNING;
    public static Item ABILITY_BLINK;
    public static Item ABILITY_HOLY_SMITE;
    public static Item ABILITY_SMOKE_BOMB;
    public static Item ABILITY_FORTIFY;

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
        ASSAULT_RIFLE = gun("assault_rifle", 10.0, 5);
        SNIPER_RIFLE = gun("sniper_rifle", 24.0, 40);
        LMG = gun("lmg", 6.0, 3);
        KATANA = melee("katana", 9.0, -2.0);
        GREATSWORD = melee("greatsword", 14.0, -3.2);
        SPEAR = melee("spear", 7.0, -2.6);
        GLAIVE = melee("glaive", 11.0, -2.8);
        RAPIER = melee("rapier", 5.0, -1.4);
        CLAYMORE = melee("claymore", 12.0, -3.0);
        WAR_DAGGER = melee("war_dagger", 4.0, -1.0);
        SCYTHE = melee("scythe", 10.0, -2.4);
        AMBROSIUM_SHARD = ambrosiumShard("ambrosium_shard");
        SHIELD_WOODEN = shield("shield_wooden", 120);
        SHIELD_IRON = shield("shield_iron", 300);
        SHIELD_GOLDEN = shield("shield_golden", 200);
        SHIELD_DIAMOND = shield("shield_diamond", 500);
        SHIELD_NETHERITE = shield("shield_netherite", 800);
        ABILITY_WARCRY = ability("ability_warcry", com.buildcraftneo.data.Job.WARRIOR, 20, 200, Abilities::warcry);
        ABILITY_TAUNT = ability("ability_taunt", com.buildcraftneo.data.Job.GUARDIAN, 15, 200, Abilities::taunt);
        ABILITY_SHADOWSTEP = ability("ability_shadowstep", com.buildcraftneo.data.Job.ASSASSIN, 15, 100, Abilities::shadowstep);
        ABILITY_SANCTUARY = ability("ability_sanctuary", com.buildcraftneo.data.Job.HEALER, 25, 200, Abilities::sanctuary);
        ABILITY_METEOR = ability("ability_meteor", com.buildcraftneo.data.Job.MAGE, 40, 300, Abilities::meteor);
        ABILITY_FROST_NOVA = ability("ability_frost_nova", com.buildcraftneo.data.Job.MAGE, 30, 300, Abilities::frostNova);
        ABILITY_RAIN_OF_ARROWS = ability("ability_rain_of_arrows", com.buildcraftneo.data.Job.ARCHER, 30, 300, Abilities::rainOfArrows);
        ABILITY_PIERCE_SHOT = ability("ability_pierce_shot", com.buildcraftneo.data.Job.ARCHER, 15, 100, Abilities::pierceShot);
        ABILITY_HEAL = ability("ability_heal", com.buildcraftneo.data.Job.HEALER, 20, 160, Abilities::heal);
        ABILITY_LIGHTNING = ability("ability_lightning", com.buildcraftneo.data.Job.MAGE, 30, 200, Abilities::lightning);
        ABILITY_BLINK = ability("ability_blink", com.buildcraftneo.data.Job.ASSASSIN, 10, 80, Abilities::blink);
        ABILITY_HOLY_SMITE = ability("ability_holy_smite", com.buildcraftneo.data.Job.GUARDIAN, 30, 240, Abilities::holySmite);
        ABILITY_SMOKE_BOMB = ability("ability_smoke_bomb", com.buildcraftneo.data.Job.ASSASSIN, 15, 200, Abilities::smokeBomb);
        ABILITY_FORTIFY = ability("ability_fortify", com.buildcraftneo.data.Job.GUARDIAN, 25, 300, Abilities::fortify);

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

    private static Item melee(String name, double damage, double speed) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        var mods = net.minecraft.world.item.component.ItemAttributeModifiers.builder()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                        new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID, damage - 1.0,
                                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE),
                        net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,
                        new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID, speed,
                                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE),
                        net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND)
                .build();
        return Registry.register(BuiltInRegistries.ITEM, id,
                new Item(new Item.Properties().setId(key).attributes(mods).stacksTo(1)));
    }

    private static Item ambrosiumShard(String name) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new AmbrosiumShardItem(new Item.Properties().setId(key)));
    }

    private static Item shield(String name, int durability) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new net.minecraft.world.item.ShieldItem(
                        new Item.Properties().setId(key).durability(durability)));
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
