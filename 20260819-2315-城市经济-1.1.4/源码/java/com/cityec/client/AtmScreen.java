package com.cityec.client;

import com.cityec.screen.AtmScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class AtmScreen extends HandledScreen<AtmScreenHandler> {
    public AtmScreen(AtmScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 360;
        backgroundHeight = 236;
    }

    @Override protected void init() {
        super.init();
        int left = (width - backgroundWidth) / 2;
        int top = (height - backgroundHeight) / 2;
        int[] values = handler.denominations();
        for (int i = 0; i < values.length; i++) {
            int column = i % 3;
            int row = i / 3;
            int id = i;
            addDrawableChild(ButtonWidget.builder(Text.literal(values[i] + "元"), button -> click(id))
                    .dimensions(left + 32 + column * 100, top + 64 + row * 28, 92, 24).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.cityec.withdraw_confirm"), button -> click(6))
                .dimensions(left + 32, top + 126, 140, 26).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.cityec.deposit_cash"), button -> click(7))
                .dimensions(left + 188, top + 126, 140, 26).build());
    }

    private void click(int id) {
        handler.onButtonClick(client.player, id);
        if (client.interactionManager != null) client.interactionManager.clickButton(handler.syncId, id);
    }

    @Override protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = (width - backgroundWidth) / 2;
        int top = (height - backgroundHeight) / 2;
        context.fill(left, top, left + backgroundWidth, top + backgroundHeight, 0xFFE5E7EB);
        context.fill(left, top, left + backgroundWidth, top + 30, 0xFF334155);
        context.drawText(textRenderer, title, left + 12, top + 10, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.atm_status", handler.enabled()), left + 24, top + 42, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.withdraw_choice", handler.selected()), left + 24, top + 56, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.configured_amount", handler.configuredAmount()), left + 200, top + 42, 0xFF0F172A, false);
        context.fill(left + 160, top + 40, left + 180, top + 60, 0xFF111827);
        context.fill(left + 161, top + 41, left + 179, top + 59, 0xFFE5E7EB);
        context.drawText(textRenderer, Text.translatable("screen.cityec.insert_card"), left + 188, top + 48, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.inventory"), left + 8, top + 146, 0xFF475569, false);
        context.fill(left + 6, top + 154, left + 178, top + 232, 0x221F2937);
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override protected void drawForeground(DrawContext context, int mouseX, int mouseY) {}
}
