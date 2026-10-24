package com.buildcraftneo.client;

import com.buildcraftneo.industry.AtmMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Simple client screen for the ATM menu: single 9-slot deposit row. */
public class AtmScreen extends AbstractContainerScreen<AtmMenu> {
    public AtmScreen(AtmMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 176, 20 + 18 + 14 + 96 + 12);
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
        g.text(font, title, leftPos + 8, topPos + 6, 0xFFE0FFD0);
        g.text(font, Component.literal("Balance: " + menu.balance() + " MD"),
                leftPos + 80, topPos + 6, 0xFFFFD070);
    }
}
