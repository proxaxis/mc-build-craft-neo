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
    public static Item ZANITE_GEMSTONE;

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
    public static Item SPELLBOOK;
    public static Item SCROLL_HEAL;
    public static Item SCROLL_FIRE;
    public static Item SCROLL_ENDER;
    public static Item SCROLL_HOLY;

    // point blank: extra guns + ammo
    public static Item SMG;
    public static Item SHOTGUN;
    public static Item AMMO;

    // aether analogue
    public static Item PARACHUTE;

    // RPG series (paladins/archers)
    public static Item ABILITY_BARRIER;
    public static Item ABILITY_CONSECRATE;
    public static Item ABILITY_VOLLEY;
    public static Item PALADIN_MACE;
    public static Item HOLY_STAFF;
    public static Item MARKSMAN_BOW;
    public static Item AP_AMMO;
    public static Item INC_AMMO;
    public static Item NAGA_SCALE;
    public static Item LICH_SCEPTER;
    public static Item ZOMBIE_SCEPTER;
    public static Item NAGA_CHESTPLATE;
    public static Item NAGA_LEGGINGS;
    public static Item LICH_CROWN;

    // IC2 powered armor (approximation: constant effects, no EU charge)
    public static Item NANO_CHESTPLATE;
    public static Item QUANTUM_CHESTPLATE;
    public static Item NANO_HELMET;
    public static Item NANO_LEGGINGS;
    public static Item NANO_BOOTS;
    public static Item QUANTUM_HELMET;
    public static Item QUANTUM_LEGGINGS;
    public static Item QUANTUM_BOOTS;

    // Point Blank attachments
    public static Item EXT_MAG;
    public static Item SCOPE;
    public static Item GRIP;
    public static Item SUPPRESSOR;

    // IC2 EU tools
    public static Item MINING_LASER;
    public static Item ELECTRIC_DRILL;
    public static Item CHAINSAW;

    // Point Blank throwable
    public static Item GRENADE;

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
        SERVICE_PISTOL = gun("service_pistol", 8.0, 8, 20);
        ASSAULT_RIFLE = gun("assault_rifle", 10.0, 5, 30);
        SNIPER_RIFLE = gun("sniper_rifle", 24.0, 40, 10);
        LMG = gun("lmg", 6.0, 3, 60);
        KATANA = melee("katana", 9.0, -2.0, 60, Abilities::katanaDash);
        GREATSWORD = melee("greatsword", 14.0, -3.2, 100, Abilities::shockwave);
        SPEAR = melee("spear", 7.0, -2.6, 60, Abilities::lunge);
        GLAIVE = melee("glaive", 11.0, -2.8, 80, Abilities::whirlwind);
        RAPIER = melee("rapier", 5.0, -1.4, 80, Abilities::flurry);
        CLAYMORE = melee("claymore", 12.0, -3.0, 100, Abilities::slam);
        WAR_DAGGER = melee("war_dagger", 4.0, -1.0, 60, Abilities::venom);
        SCYTHE = melee("scythe", 10.0, -2.4, 80, Abilities::harvest);
        AMBROSIUM_SHARD = ambrosiumShard("ambrosium_shard");
        ZANITE_GEMSTONE = zanite("zanite_gemstone");
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
        SPELLBOOK = spellbook("spellbook");
        SCROLL_HEAL = scroll("scroll_heal", 10, Abilities::heal);
        SCROLL_FIRE = scroll("scroll_fire", 15, Abilities::meteor);
        SCROLL_ENDER = scroll("scroll_ender", 8, Abilities::blink);
        SCROLL_HOLY = scroll("scroll_holy", 15, Abilities::holySmite);
        SMG = gun("smg", 7.0, 2, 40);
        SHOTGUN = gunPellets("shotgun", 4.0, 20, 6, 5);
        AMMO = simple("ammo");
        PARACHUTE = parachute("parachute");
        ABILITY_BARRIER = ability("ability_barrier", com.buildcraftneo.data.Job.PALADIN, 30, 300, Abilities::barrier);
        ABILITY_CONSECRATE = ability("ability_consecrate", com.buildcraftneo.data.Job.PRIEST, 25, 200, Abilities::consecrate);
        ABILITY_VOLLEY = ability("ability_volley", com.buildcraftneo.data.Job.MARKSMAN, 20, 120, Abilities::volley);
        PALADIN_MACE = melee("paladin_mace", 11.0, -2.6, 80, Abilities::holySmite);
        HOLY_STAFF = ability("holy_staff", com.buildcraftneo.data.Job.PRIEST, 15, 160, Abilities::sanctuary);
        MARKSMAN_BOW = bow("marksman_bow", 14.0, 12);
        AP_AMMO = simple("ap_ammo");
        INC_AMMO = simple("inc_ammo");
        NAGA_SCALE = simple("naga_scale");
        LICH_SCEPTER = lichScepter("lich_scepter");
        ZOMBIE_SCEPTER = zombieScepter("zombie_scepter");
        NAGA_CHESTPLATE = armor("naga_chestplate",
                net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
                net.minecraft.world.item.equipment.ArmorType.CHESTPLATE);
        NAGA_LEGGINGS = armor("naga_leggings",
                net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
                net.minecraft.world.item.equipment.ArmorType.LEGGINGS);
        LICH_CROWN = armor("lich_crown",
                net.minecraft.world.item.equipment.ArmorMaterials.GOLD,
                net.minecraft.world.item.equipment.ArmorType.HELMET);
        NANO_CHESTPLATE = armor("nano_chestplate",
                net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
                net.minecraft.world.item.equipment.ArmorType.CHESTPLATE);
        QUANTUM_CHESTPLATE = armor("quantum_chestplate",
                net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
                net.minecraft.world.item.equipment.ArmorType.CHESTPLATE);
        NANO_HELMET = armor("nano_helmet",
                net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
                net.minecraft.world.item.equipment.ArmorType.HELMET);
        NANO_LEGGINGS = armor("nano_leggings",
                net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
                net.minecraft.world.item.equipment.ArmorType.LEGGINGS);
        NANO_BOOTS = armor("nano_boots",
                net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
                net.minecraft.world.item.equipment.ArmorType.BOOTS);
        QUANTUM_HELMET = armor("quantum_helmet",
                net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
                net.minecraft.world.item.equipment.ArmorType.HELMET);
        QUANTUM_LEGGINGS = armor("quantum_leggings",
                net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
                net.minecraft.world.item.equipment.ArmorType.LEGGINGS);
        QUANTUM_BOOTS = armor("quantum_boots",
                net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
                net.minecraft.world.item.equipment.ArmorType.BOOTS);
        EXT_MAG = simple("ext_mag");
        SCOPE = simple("scope");
        GRIP = simple("grip");
        SUPPRESSOR = simple("suppressor");
        {
            Identifier id = BCN.id("mining_laser");
            MINING_LASER = Registry.register(BuiltInRegistries.ITEM, id,
                    new MiningLaserItem(new Item.Properties().stacksTo(1)
                            .setId(ResourceKey.create(Registries.ITEM, id))));
        }
        {
            Identifier id = BCN.id("electric_drill");
            ELECTRIC_DRILL = Registry.register(BuiltInRegistries.ITEM, id,
                    new ElectricDrillItem(new Item.Properties().stacksTo(1)
                            .setId(ResourceKey.create(Registries.ITEM, id))));
        }
        {
            Identifier id = BCN.id("chainsaw");
            CHAINSAW = Registry.register(BuiltInRegistries.ITEM, id,
                    new ChainsawItem(new Item.Properties().stacksTo(1)
                            .setId(ResourceKey.create(Registries.ITEM, id))));
        }
        {
            Identifier id = BCN.id("grenade");
            GRENADE = Registry.register(BuiltInRegistries.ITEM, id,
                    new GrenadeItem(new Item.Properties().stacksTo(16)
                            .setId(ResourceKey.create(Registries.ITEM, id))));
        }

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

    private static Item gun(String name, double damage, int cooldownTicks, int magazine) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new GunItem(new Item.Properties().setId(key).durability(magazine),
                        damage, cooldownTicks));
    }

    private static Item melee(String name, double damage, double speed, int cooldown,
            java.util.function.BiConsumer<net.minecraft.server.level.ServerPlayer,
                    net.minecraft.server.level.ServerLevel> power) {
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
                new PowerWeaponItem(new Item.Properties().setId(key)
                        .attributes(mods).stacksTo(1), cooldown, power));
    }

    private static Item zanite(String name) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new ZaniteGemstoneItem(new Item.Properties().setId(key)));
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

    private static Item gunPellets(String name, double damage, int cooldownTicks,
            int magazine, int pellets) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new GunItem(new Item.Properties().setId(key).durability(magazine),
                        damage, cooldownTicks, pellets));
    }

    private static Item spellbook(String name) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new SpellbookItem(new Item.Properties().setId(key).stacksTo(1)));
    }

    private static Item scroll(String name, int mpCost,
            java.util.function.BiConsumer<net.minecraft.server.level.ServerPlayer,
                    net.minecraft.server.level.ServerLevel> effect) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new ScrollItem(new Item.Properties().setId(key).stacksTo(16), mpCost, effect));
    }

    private static Item parachute(String name) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new ParachuteItem(new Item.Properties().setId(key).durability(12).stacksTo(1)));
    }

    private static Item bow(String name, double damage, int cooldownTicks) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new SimpleBowItem(new Item.Properties().setId(key).durability(384), damage, cooldownTicks));
    }

    private static Item armor(String name,
            net.minecraft.world.item.equipment.ArmorMaterial material,
            net.minecraft.world.item.equipment.ArmorType type) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new Item(new Item.Properties().setId(key).stacksTo(1)
                        .humanoidArmor(material, type)));
    }

    /** Twilight Forest-style zombie scepter: summons three zombie minions. */
    private static Item zombieScepter(String name) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new SpellItem(new Item.Properties().setId(key).stacksTo(1), 25) {
                    @Override
                    protected void cast(net.minecraft.server.level.ServerPlayer caster) {
                        Abilities.summonZombies(caster,
                                (net.minecraft.server.level.ServerLevel) caster.level());
                    }
                });
    }

    /** Twilight Forest-style Lich drop: reusable scepter that casts lightning. */
    private static Item lichScepter(String name) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new SpellItem(new Item.Properties().setId(key).stacksTo(1), 20) {
                    @Override
                    protected void cast(net.minecraft.server.level.ServerPlayer caster) {
                        Abilities.lightning(caster,
                                (net.minecraft.server.level.ServerLevel) caster.level());
                    }
                });
    }
}
