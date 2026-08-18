package com.cityec.block.entity;

import com.cityec.screen.ConfigScreenHandler;
import com.cityec.screen.RegisterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class CashRegisterBlockEntity extends BlockEntity {
    public boolean enabled = true;
    public int price = 100;

    public CashRegisterBlockEntity(BlockPos pos, BlockState state) { super(CityEconomyBlockEntities.CASH_REGISTER, pos, state); }

    public ScreenHandler createConfigHandler(int syncId, PlayerInventory inventory) {
        return new ConfigScreenHandler(syncId, inventory, this, false);
    }

    public ScreenHandler createRegisterHandler(int syncId, PlayerInventory inventory) {
        return new RegisterScreenHandler(syncId, inventory, this);
    }

    public void applyConfig(boolean enabled, int price) {
        this.enabled = enabled;
        this.price = Math.max(1, price);
        if (world != null) {
            updateActiveState(pos, enabled);
        }
        markDirty();
    }

    private void updateActiveState(BlockPos blockPos, boolean active) {
        BlockState state = world.getBlockState(blockPos);
        if (state.contains(com.cityec.block.TallMachineBlock.ACTIVE)) {
            world.setBlockState(blockPos, state.with(com.cityec.block.TallMachineBlock.ACTIVE, active), net.minecraft.block.Block.NOTIFY_ALL);
        }
    }

    @Override protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("enabled", enabled);
        nbt.putInt("price", price);
    }

    @Override public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        enabled = nbt.getBoolean("enabled");
        price = nbt.contains("price") ? nbt.getInt("price") : 100;
    }
}
