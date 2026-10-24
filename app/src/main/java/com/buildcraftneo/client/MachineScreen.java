package com.buildcraftneo.client;

import com.buildcraftneo.industry.MachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Simple client screen for the generic machine menu: one slot row + player inv. */
public class MachineScreen extends AbstractContainerScreen<MachineMenu> {
    public MachineScreen(MachineMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 176, 20 + 18 + 14 + 96 + 12);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int x, int y, float delta) {
        g.fill(leftPos - 2, topPos - 2, leftPos + imageWidth + 2,
                topPos + imageHeight + 2, 0xFF2B2B3B);
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF6B6B7B);
        for (int i = 0; i < menu.machineSlots(); i++) {
            int sx = leftPos + 8 + i * 18 - 1;
            g.fill(sx, topPos + 19, sx + 18, topPos + 19 + 18, 0xFF4A4A5A);
        }
        for (int i = 0; i < 36; i++) {
            int sx = leftPos + 8 + (i % 9) * 18 - 1;
            int sy = topPos + 20 + 18 + 14 + (i / 9) * 18 - 1;
            g.fill(sx, sy, sx + 18, sy + 18, 0xFF55556A);
        }
        g.text(font, title, leftPos + 8, topPos + 6, 0xFF404040);
    }
}
