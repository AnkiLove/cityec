package com.cityec.block.entity;

import com.cityec.screen.ConfigScreenHandler;
import com.cityec.screen.AtmScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class AtmBlockEntity extends BlockEntity {
    public boolean enabled = true;
    public int withdrawAmount = 100;

    public AtmBlockEntity(BlockPos pos, BlockState state) { super(CityEconomyBlockEntities.ATM, pos, state); }

    public ScreenHandler createConfigHandler(int syncId, PlayerInventory inventory) {
        return new ConfigScreenHandler(syncId, inventory, this, true);
    }

    public ScreenHandler createAtmHandler(int syncId, PlayerInventory inventory) {
        return new AtmScreenHandler(syncId, inventory, this);
    }

    public void applyConfig(boolean enabled, int amount) {
        this.enabled = enabled;
        this.withdrawAmount = Math.max(1, amount);
        if (world != null) {
            updateActiveState(pos, enabled);
            updateActiveState(pos.up(), enabled);
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
        nbt.putInt("withdraw_amount", withdrawAmount);
    }

    @Override public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        enabled = nbt.getBoolean("enabled");
        withdrawAmount = nbt.contains("withdraw_amount") ? nbt.getInt("withdraw_amount") : 100;
    }
}
