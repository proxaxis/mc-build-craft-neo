package com.buildcraftneo.client;

import com.buildcraftneo.industry.BCNMenus;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class BuildCraftNeoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(BCNMenus.SHOP, ShopScreen::new);
    }
}
