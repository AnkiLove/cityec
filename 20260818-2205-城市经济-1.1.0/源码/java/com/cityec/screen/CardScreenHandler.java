package com.cityec.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;

public class CardScreenHandler extends ScreenHandler {
    private final int balance;

    public CardScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        super(CityEconomyScreenHandlers.CARD, syncId);
        balance = buf.readInt();
    }

    public CardScreenHandler(int syncId, PlayerInventory inventory, int balance) {
        super(CityEconomyScreenHandlers.CARD, syncId);
        this.balance = balance;
    }

    public int balance() { return balance; }
    @Override public boolean canUse(PlayerEntity player) { return true; }
    @Override public ItemStack quickMove(PlayerEntity player, int slot) { return ItemStack.EMPTY; }
}
