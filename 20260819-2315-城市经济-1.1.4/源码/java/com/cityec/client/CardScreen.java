package com.cityec.client;

import com.cityec.screen.CardScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class CardScreen extends HandledScreen<CardScreenHandler> {
    public CardScreen(CardScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 300;
        backgroundHeight = 140;
    }

    @Override protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = (width - backgroundWidth) / 2;
        int top = (height - backgroundHeight) / 2;
        context.fill(left, top, left + backgroundWidth, top + backgroundHeight, 0xFFD1D5DB);
        context.fill(left, top, left + backgroundWidth, top + 30, 0xFF334155);
        context.drawText(textRenderer, title, left + 12, top + 10, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.card_balance", handler.balance()), left + 32, top + 62, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.card_hint"), left + 32, top + 92, 0xFF334155, false);
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override protected void drawForeground(DrawContext context, int mouseX, int mouseY) {}
}
