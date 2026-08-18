package com.cityec.screen;

import com.cityec.block.entity.AtmBlockEntity;
import com.cityec.economy.EconomyUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class AtmScreenHandler extends ScreenHandler {
    private static final int[] DENOMINATIONS = {100, 50, 20, 10, 5, 1};
    private final AtmBlockEntity target;
    private final BlockPos pos;
    private final boolean enabled;
    private final int configuredAmount;
    private final net.minecraft.inventory.SimpleInventory cardInventory = new net.minecraft.inventory.SimpleInventory(1);
    private int selected = 100;

    public AtmScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        super(CityEconomyScreenHandlers.ATM, syncId);
        this.target = null;
        this.pos = buf.readBlockPos();
        this.configuredAmount = buf.readInt();
        this.enabled = buf.readBoolean();
        addSlot(new BankCardSlot(cardInventory, 0, 162, 42));
        addPlayerInventory(inventory);
    }

    public AtmScreenHandler(int syncId, PlayerInventory inventory, AtmBlockEntity target) {
        super(CityEconomyScreenHandlers.ATM, syncId);
        this.target = target;
        this.pos = target.getPos();
        this.configuredAmount = target.withdrawAmount;
        this.enabled = target.enabled;
        addSlot(new BankCardSlot(cardInventory, 0, 162, 42));
        addPlayerInventory(inventory);
    }

    public int selected() { return selected; }
    public int configuredAmount() { return configuredAmount; }
    public boolean enabled() { return enabled; }
    public int[] denominations() { return DENOMINATIONS; }
    public ItemStack insertedCard() { return cardInventory.getStack(0); }

    @Override public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < DENOMINATIONS.length) {
            selected = DENOMINATIONS[id];
            return true;
        }
        if (player.getWorld().isClient) return id == 6 || id == 7;
        if (id == 6) {
            if (!enabled) { player.sendMessage(Text.translatable("cityec.message.atm_off"), true); return true; }
            ItemStack card = insertedCard();
            if (card.getItem() instanceof com.cityec.item.ValueCardItem) {
                if (EconomyUtil.chargeCard(card, selected)) {
                    EconomyUtil.giveCash(player, selected);
                    player.sendMessage(Text.translatable("cityec.message.withdraw", selected, EconomyUtil.getCardBalance(card)), true);
                } else player.sendMessage(Text.translatable("cityec.message.insufficient"), true);
            } else player.sendMessage(Text.translatable("cityec.message.insert_card"), true);
            return true;
        }
        if (id == 7) {
            if (!enabled) {
                player.sendMessage(Text.translatable("cityec.message.atm_off"), true);
            } else if (insertedCard().getItem() instanceof com.cityec.item.ValueCardItem) {
                EconomyUtil.depositOneCashToCard(player, player.getMainHandStack(), insertedCard());
            } else player.sendMessage(Text.translatable("cityec.message.insert_card"), true);
            return true;
        }
        return false;
    }

    @Override public boolean canUse(PlayerEntity player) {
        return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    private void addPlayerInventory(PlayerInventory inventory) {
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new net.minecraft.screen.slot.Slot(inventory, col + row * 9 + 9, 8 + col * 18, 158 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new net.minecraft.screen.slot.Slot(inventory, col, 8 + col * 18, 212));
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
