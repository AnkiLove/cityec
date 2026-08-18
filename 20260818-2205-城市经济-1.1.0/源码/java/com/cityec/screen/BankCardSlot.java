package com.cityec.screen;

import com.cityec.item.ValueCardItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class BankCardSlot extends Slot {
    public BankCardSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override public boolean canInsert(ItemStack stack) { return stack.getItem() instanceof ValueCardItem; }
    @Override public int getMaxItemCount() { return 1; }
    @Override public boolean canTakeItems(PlayerEntity player) { return true; }
}
