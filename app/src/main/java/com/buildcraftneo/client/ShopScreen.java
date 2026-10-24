package com.buildcraftneo.client;

import com.buildcraftneo.industry.ShopMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Simple client screen for the shop menu: grey panel, 9x3 slot grid. */
public class ShopScreen extends AbstractContainerScreen<ShopMenu> {
    /** Last sale-history lines pushed by the server (see ShopHistoryPayload). */
    private static volatile java.util.List<String> history = java.util.List.of();

    public static void onHistory(java.util.List<String> lines) {
        history = lines;
    }

    public ShopScreen(ShopMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 176, 72 + 16 + 96);
        history = java.util.List.of();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int x, int y, float delta) {
        g.fill(leftPos - 2, topPos - 2, leftPos + imageWidth + 2,
                topPos + imageHeight + 2, 0xFF2B2B2B);
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF8B8B8B);
        for (int i = 0; i < menu.shopSlots(); i++) {
            int sx = leftPos + 8 + (i % 9) * 18 - 1;
            int sy = topPos + 18 + (i / 9) * 18 - 1;
            g.fill(sx, sy, sx + 18, sy + 18, 0xFF5A5A5A);
        }
        for (int i = 0; i < 36; i++) {
            int sx = leftPos + 8 + (i % 9) * 18 - 1;
            int sy = topPos + 18 + 3 * 18 + 14 + (i / 9) * 18 - 1;
            g.fill(sx, sy, sx + 18, sy + 18, 0xFF6A6A6A);
        }
        g.text(font, title, leftPos + 8, topPos + 6, 0xFF404040);
        // sale-history side panel, fed by ShopHistoryPayload
        var lines = history;
        if (!lines.isEmpty()) {
            int hx = leftPos + imageWidth + 6;
            int hy = topPos;
            int hw = 150;
            int hh = 14 + lines.size() * 10;
            g.fill(hx, hy, hx + hw, hy + hh, 0xCC202028);
            g.text(font, "History", hx + 4, hy + 4, 0xFFDDDDDD);
            for (int i = 0; i < lines.size(); i++) {
                g.text(font, lines.get(i), hx + 4, hy + 14 + i * 10, 0xFFAAAAAA);
            }
        }
    }
}
