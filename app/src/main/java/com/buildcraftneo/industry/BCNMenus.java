package com.buildcraftneo.industry;

import com.buildcraftneo.BCN;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class BCNMenus {
    private BCNMenus() {}

    public static MenuType<ShopMenu> SHOP;

    public static void register() {
        SHOP = Registry.register(BuiltInRegistries.MENU, BCN.id("shop"),
                new MenuType<>(ShopMenu::new, FeatureFlags.VANILLA_SET));
    }
}
