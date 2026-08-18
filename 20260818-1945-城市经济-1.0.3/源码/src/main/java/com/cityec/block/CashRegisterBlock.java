package com.cityec.block;

import com.cityec.block.entity.CashRegisterBlockEntity;
import com.cityec.item.CityEconomyItems;
import com.cityec.economy.EconomyUtil;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.network.PacketByteBuf;

public class CashRegisterBlock extends TallMachineBlock {
    public CashRegisterBlock(Settings settings) { super(settings); }

    @Override public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return state.get(HALF) == net.minecraft.block.enums.DoubleBlockHalf.UPPER ? null : new CashRegisterBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockPos lower = lowerPos(pos, state);
        CashRegisterBlockEntity register = (CashRegisterBlockEntity) world.getBlockEntity(lower);
        if (register == null) return ActionResult.PASS;
        ItemStack held = player.getStackInHand(hand);
        if (held.isOf(CityEconomyItems.WRENCH)) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override public void writeScreenOpeningData(ServerPlayerEntity p, PacketByteBuf buf) {
                    buf.writeBlockPos(pos);
                    buf.writeBoolean(false);
                    buf.writeBoolean(register.enabled);
                    buf.writeInt(register.price);
                }
                @Override public Text getDisplayName() { return Text.translatable("screen.cityec.register_config"); }
                @Override public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) { return register.createConfigHandler(syncId, inv); }
            });
            return ActionResult.CONSUME;
        }
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override public void writeScreenOpeningData(ServerPlayerEntity p, PacketByteBuf buf) {
                buf.writeBlockPos(lower);
                buf.writeInt(register.price);
                buf.writeBoolean(register.enabled);
            }
            @Override public Text getDisplayName() { return Text.translatable("screen.cityec.register"); }
            @Override public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) { return register.createRegisterHandler(syncId, inv); }
        });
        return ActionResult.CONSUME;
    }
}
