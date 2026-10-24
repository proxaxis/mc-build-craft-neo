package com.buildcraftneo.client;

import com.buildcraftneo.BCN;
import com.buildcraftneo.industry.BCNBlocks;
import com.buildcraftneo.industry.BCNMenus;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class BuildCraftNeoClient implements ClientModInitializer {
    private static final Map<Block, Identifier> PIPE_TEXTURES = Map.of(
            BCNBlocks.ITEM_PIPE, BCN.id("block/pipe_iron"),
            BCNBlocks.TELEPORT_PIPE, BCN.id("block/pipe_diamond"),
            BCNBlocks.REQUEST_PIPE, BCN.id("block/pipe_wood"),
            BCNBlocks.SUPPLIER_PIPE, BCN.id("block/pipe_supplier"),
            BCNBlocks.SORTING_PIPE, BCN.id("block/pipe_basic"),
            BCNBlocks.CRAFTING_PIPE, BCN.id("block/pipe_crafting"));

    @Override
    public void onInitializeClient() {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
                .registerGlobalReceiver(
                        com.buildcraftneo.net.ShopHistoryPayload.TYPE,
                        (payload, context) -> ShopScreen.onHistory(payload.lines()));
        MenuScreens.register(BCNMenus.SHOP, ShopScreen::new);
        MenuScreens.register(BCNMenus.ATM, AtmScreen::new);
        MenuScreens.register(BCNMenus.MACHINE, MachineScreen::new);
        ModelLoadingPlugin.register(context -> context.modifyBlockModelAfterBake()
                .register((model, ctx) -> {
                    Identifier tex = PIPE_TEXTURES.get(ctx.state().getBlock());
                    if (tex == null) {
                        return model;
                    }
                    Material.Baked mat = ctx.baker().materials().get(
                            new Material(tex), () -> "buildcraftneo pipe");
                    return new PipeBlockStateModel(mat);
                }));
    }
}
