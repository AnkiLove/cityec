package com.cityec.screen;

import com.cityec.block.entity.AtmBlockEntity;
import com.cityec.block.entity.CashRegisterBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ConfigScreenHandler extends ScreenHandler {
    private final Object target;
    private final BlockPos targetPos;
    private final boolean atm;
    private boolean enabled;
    private int value;

    public ConfigScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        super(CityEconomyScreenHandlers.CONFIG, syncId);
        this.target = null;
        this.targetPos = buf.readBlockPos();
        this.atm = buf.readBoolean();
        this.enabled = buf.readBoolean();
        this.value = buf.readInt();
    }

    public ConfigScreenHandler(int syncId, PlayerInventory inventory, Object target, boolean atm) {
        super(CityEconomyScreenHandlers.CONFIG, syncId);
        this.target = target;
        this.targetPos = target instanceof net.minecraft.block.entity.BlockEntity entity ? entity.getPos() : BlockPos.ORIGIN;
        this.atm = atm;
        if (target instanceof AtmBlockEntity entity) { enabled = entity.enabled; value = entity.withdrawAmount; }
        else if (target instanceof CashRegisterBlockEntity entity) { enabled = entity.enabled; value = entity.price; }
        else { enabled = true; value = 100; }
    }

    public boolean isAtm() { return atm; }
    public boolean isEnabled() { return enabled; }
    public int value() { return value; }
    public BlockPos targetPos() { return targetPos; }
    public void setClientValue(int value) { this.value = Math.max(1, Math.min(1_000_000, value)); }

    @Override public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == 0) enabled = !enabled;
        if (id == 1) value = Math.max(1, value - 1);
        if (id == 2) value = Math.min(1000000, value + 1);
        if (id == 3 && target != null) {
            if (target instanceof AtmBlockEntity entity) entity.applyConfig(enabled, value);
            if (target instanceof CashRegisterBlockEntity entity) entity.applyConfig(enabled, value);
        }
        return true;
    }

    @Override public boolean canUse(PlayerEntity player) { return true; }

    @Override public ItemStack quickMove(PlayerEntity player, int slot) { return ItemStack.EMPTY; }
}
