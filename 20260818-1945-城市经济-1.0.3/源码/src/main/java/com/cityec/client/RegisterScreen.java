package com.cityec.client;

import com.cityec.screen.RegisterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class RegisterScreen extends HandledScreen<RegisterScreenHandler> {
    public RegisterScreen(RegisterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 320;
        backgroundHeight = 190;
    }

    @Override protected void init() {
        super.init();
        int left = (width - backgroundWidth) / 2;
        int top = (height - backgroundHeight) / 2;
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.cityec.pay_cash"), button -> click(0))
                .dimensions(left + 22, top + 76, 132, 28).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.cityec.pay_card"), button -> click(1))
                .dimensions(left + 166, top + 76, 132, 28).build());
    }

    private void click(int id) {
        if (client.interactionManager != null) client.interactionManager.clickButton(handler.syncId, id);
    }

    @Override protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = (width - backgroundWidth) / 2;
        int top = (height - backgroundHeight) / 2;
        context.fill(left, top, left + backgroundWidth, top + backgroundHeight, 0xFFE5E7EB);
        context.fill(left, top, left + backgroundWidth, top + 30, 0xFF334155);
        context.drawText(textRenderer, title, left + 12, top + 10, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.register_status", handler.enabled()), left + 22, top + 48, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.register_price", handler.price()), left + 22, top + 66, 0xFF0F172A, false);
        context.fill(left + 150, top + 40, left + 170, top + 60, 0xFF111827);
        context.fill(left + 151, top + 41, left + 169, top + 59, 0xFFE5E7EB);
        context.drawText(textRenderer, Text.translatable("screen.cityec.insert_card"), left + 178, top + 48, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.inventory"), left + 8, top + 100, 0xFF475569, false);
        context.fill(left + 6, top + 108, left + 178, top + 186, 0x221F2937);
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override protected void drawForeground(DrawContext context, int mouseX, int mouseY) {}
}
