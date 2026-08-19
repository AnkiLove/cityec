package com.cityec.client;

import com.cityec.screen.ConfigScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.text.Text;

public class ConfigScreen extends HandledScreen<ConfigScreenHandler> {
    private TextFieldWidget amountField;
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
        amountField = new TextFieldWidget(textRenderer, left + 92, top + 88, 132, 20, Text.translatable("screen.cityec.input_amount"));
        amountField.setMaxLength(7);
        amountField.setText(Integer.toString(handler.value()));
        amountField.setTextPredicate(value -> value.chars().allMatch(Character::isDigit));
        amountField.setChangedListener(value -> {
            if (!value.isEmpty()) {
                try { handler.setClientValue(Integer.parseInt(value)); } catch (NumberFormatException ignored) { }
            }
        });
        addDrawableChild(amountField);
        setInitialFocus(amountField);
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.cityec.save"), button -> save())
                .dimensions(left + 95, top + 135, 110, 24).build());
    }

    private void click(int id) {
        handler.onButtonClick(client.player, id);
        if (client.interactionManager != null) client.interactionManager.clickButton(handler.syncId, id);
    }

    private void save() {
        int value = 1;
        try { value = Integer.parseInt(amountField.getText()); } catch (NumberFormatException ignored) { }
        value = Math.max(1, Math.min(1_000_000, value));
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(handler.targetPos());
        buf.writeBoolean(handler.isAtm());
        buf.writeBoolean(handler.isEnabled());
        buf.writeInt(value);
        ClientPlayNetworking.send(com.cityec.CityEconomyMod.CONFIG_PACKET, buf);
        close();
    }

    @Override protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = (width - backgroundWidth) / 2;
        int top = (height - backgroundHeight) / 2;
        context.fill(left, top, left + backgroundWidth, top + backgroundHeight, 0xFFEEF2F5);
        context.fill(left, top, left + backgroundWidth, top + 28, 0xFF334155);
        context.drawText(textRenderer, title, left + 12, top + 9, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.status", handler.isEnabled()), left + 20, top + 36, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable(handler.isAtm() ? "screen.cityec.withdraw_amount" : "screen.cityec.price", handler.value()), left + 20, top + 94, 0xFF0F172A, false);
        context.drawText(textRenderer, Text.translatable("screen.cityec.unit"), left + 230, top + 94, 0xFF0F172A, false);
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override protected void drawForeground(DrawContext context, int mouseX, int mouseY) {}
}
