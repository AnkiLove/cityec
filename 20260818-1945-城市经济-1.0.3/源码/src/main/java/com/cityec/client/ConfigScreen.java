package com.cityec.client;

import com.cityec.screen.ConfigScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ConfigScreen extends HandledScreen<ConfigScreenHandler> {
    public ConfigScreen(ConfigScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 300;
        this.backgroundHeight = 180;
    }

    @Override protected void init() {
        super.init();
        int left = (width - backgroundWidth) / 2;
        int top = (height - backgroundHeight) / 2;
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.cityec.toggle"), button -> click(0))
                .dimensions(left + 20, top + 50, 120, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("-"), button -> click(1))
                .dimensions(left + 20, top + 90, 36, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), button -> click(2))
                .dimensions(left + 244, top + 90, 36, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.cityec.save"), button -> click(3))
                .dimensions(left + 95, top + 135, 110, 24).build());
    }

    private void click(int id) {
        handler.onButtonClick(client.player, id);
        if (client.interactionManager != null) client.interactionManager.clickButton(handler.syncId, id);
    }

    @Override protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = (width - backgroundWidth) / 2;
        int top = (height - backgroundHeight) / 2;
        context.fill(left, top, left + backgroundWidth, top + backgroundHeight, 0xFFEEF2F5);
        context.fill(left, top, left + backgroundWidth, top + 28, 0xFF334155);
        context.drawText(textRenderer, title, left + 12, top + 9, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.status", handler.isEnabled()), left + 20, top + 36, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable(handler.isAtm() ? "screen.cityec.withdraw_amount" : "screen.cityec.price", handler.value()), left + 80, top + 98, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.unit"), left + 208, top + 98, 0xFF0F172A, false);
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override protected void drawForeground(DrawContext context, int mouseX, int mouseY) {}
}
