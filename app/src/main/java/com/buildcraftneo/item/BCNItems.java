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

    // magic tokens (placeholder wand for restriction testing)
    public static Item APPRENTICE_WAND;

    // science weapons (placeholder gun for restriction testing)
    public static Item SERVICE_PISTOL;

    public static void register() {
        COPPER_COIN = simple("copper_coin");
        IRON_COIN = simple("iron_coin");
        GOLD_COIN = simple("gold_coin");
        EMERALD_COIN = simple("emerald_coin");
        DIAMOND_COIN = simple("diamond_coin");
        NETHERITE_COIN = simple("netherite_coin");
        WRENCH = simple("wrench");
        CIRCUIT = simple("circuit");
        APPRENTICE_WAND = spell("apprentice_wand", new Item.Properties());
        SERVICE_PISTOL = gun("service_pistol");
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

    private static Item gun(String name) {
        Identifier id = BCN.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, id, new GunItem(new Item.Properties().setId(key)));
    }
}
