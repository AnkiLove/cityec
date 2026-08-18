package com.cityec.screen;

import com.cityec.block.entity.CashRegisterBlockEntity;
import com.cityec.economy.EconomyUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class RegisterScreenHandler extends ScreenHandler {
    private final CashRegisterBlockEntity target;
    private final BlockPos pos;
    private final boolean enabled;
    private final int price;
    private final net.minecraft.inventory.SimpleInventory cardInventory = new net.minecraft.inventory.SimpleInventory(1);

    public RegisterScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        super(CityEconomyScreenHandlers.REGISTER, syncId);
        target = null;
        pos = buf.readBlockPos();
        price = buf.readInt();
        enabled = buf.readBoolean();
        addSlot(new BankCardSlot(cardInventory, 0, 152, 42));
        addPlayerInventory(inventory);
    }

    public RegisterScreenHandler(int syncId, PlayerInventory inventory, CashRegisterBlockEntity target) {
        super(CityEconomyScreenHandlers.REGISTER, syncId);
        this.target = target;
        this.pos = target.getPos();
        this.price = target.price;
        this.enabled = target.enabled;
        addSlot(new BankCardSlot(cardInventory, 0, 152, 42));
        addPlayerInventory(inventory);
    }

    public int price() { return price; }
    public boolean enabled() { return enabled; }

    @Override public boolean onButtonClick(PlayerEntity player, int id) {
        if (player.getWorld().isClient) return id == 0 || id == 1;
        if (!enabled) { player.sendMessage(Text.translatable("cityec.message.register_off"), true); return true; }
        if (id == 0) {
            int paid = EconomyUtil.payCash(player, price);
            if (paid < 0) player.sendMessage(Text.translatable("cityec.message.need_more", price), true);
            else {
                EconomyUtil.giveCash(player, paid - price);
                player.sendMessage(Text.translatable("cityec.message.paid_cash", price, paid - price), true);
            }
            return true;
        }
        if (id == 1) {
            ItemStack card = cardInventory.getStack(0);
            if (card.getItem() instanceof com.cityec.item.ValueCardItem) {
                if (EconomyUtil.chargeCard(card, price)) player.sendMessage(Text.translatable("cityec.message.paid_card", price, EconomyUtil.getCardBalance(card)), true);
                else player.sendMessage(Text.translatable("cityec.message.insufficient"), true);
            } else player.sendMessage(Text.translatable("cityec.message.insert_card"), true);
            return true;
        }
        return false;
    }

    @Override public boolean canUse(PlayerEntity player) { return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0; }
    private void addPlayerInventory(PlayerInventory inventory) {
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new net.minecraft.screen.slot.Slot(inventory, col + row * 9 + 9, 8 + col * 18, 112 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new net.minecraft.screen.slot.Slot(inventory, col, 8 + col * 18, 166));
    }

    @Override public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        net.minecraft.screen.slot.Slot slot = slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index == 0) { if (!insertItem(stack, 1, slots.size(), true)) return ItemStack.EMPTY; }
            else if (stack.getItem() instanceof com.cityec.item.ValueCardItem) { if (!insertItem(stack, 0, 1, false)) return ItemStack.EMPTY; }
            else return ItemStack.EMPTY;
            if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY); else slot.markDirty();
        }
        return copy;
    }

    @Override public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        ItemStack card = cardInventory.removeStack(0);
        if (!card.isEmpty()) player.getInventory().offerOrDrop(card);
    }
}
