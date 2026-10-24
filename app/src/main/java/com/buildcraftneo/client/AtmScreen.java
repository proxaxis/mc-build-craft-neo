package com.buildcraftneo.client;

import com.buildcraftneo.industry.AtmMenu;
import com.buildcraftneo.net.AtmActionPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client screen for the ATM menu: 9-slot deposit row, player inventory, and a
 * right-side action panel with Deposit All / Withdraw 500 buttons (sent as a
 * C2S {@link AtmActionPayload}).
 */
public class AtmScreen extends AbstractContainerScreen<AtmMenu> {
    public AtmScreen(AtmMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 224, 20 + 18 + 14 + 96 + 12);
    }

    @Override
    protected void init() {
        super.init();
        int px = leftPos + 180;
        addRenderableWidget(Button.builder(Component.literal("Deposit"),
                        b -> net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
                                .send(new AtmActionPayload(AtmActionPayload.DEPOSIT_ALL)))
                .bounds(px, topPos + 20, 40, 18).build());
        addRenderableWidget(Button.builder(Component.literal("Wd 500"),
                        b -> net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
                                .send(new AtmActionPayload(AtmActionPayload.WITHDRAW_500)))
                .bounds(px, topPos + 42, 40, 18).build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int x, int y, float delta) {
        g.fill(leftPos - 2, topPos - 2, leftPos + imageWidth + 2,
                topPos + imageHeight + 2, 0xFF1B3A2B);
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF3B5B4B);
        for (int i = 0; i < menu.depositSlots(); i++) {
            int sx = leftPos + 8 + i * 18 - 1;
            g.fill(sx, topPos + 19, sx + 18, topPos + 19 + 18, 0xFF2A4A3A);
        }
        for (int i = 0; i < 36; i++) {
            int sx = leftPos + 8 + (i % 9) * 18 - 1;
            int sy = topPos + 20 + 18 + 14 + (i / 9) * 18 - 1;
            g.fill(sx, sy, sx + 18, sy + 18, 0xFF4A6A5A);
        }
        // right-side action panel
        g.fill(leftPos + 178, topPos + 14, leftPos + imageWidth - 4,
                topPos + imageHeight - 4, 0xFF2A4A3A);
        g.text(font, Component.literal("Actions"), leftPos + 182, topPos + 8, 0xFFE0FFD0);
        g.text(font, title, leftPos + 8, topPos + 6, 0xFFE0FFD0);
        g.text(font, Component.literal("Balance: " + menu.balance() + " MD"),
                leftPos + 80, topPos + 6, 0xFFFFD070);
    }
}
