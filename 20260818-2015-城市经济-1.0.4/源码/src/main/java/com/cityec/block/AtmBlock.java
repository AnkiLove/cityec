package com.cityec.block;

import com.cityec.block.entity.AtmBlockEntity;
import com.cityec.block.entity.CityEconomyBlockEntities;
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

public class AtmBlock extends TallMachineBlock {
    public AtmBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return state.get(HALF) == net.minecraft.block.enums.DoubleBlockHalf.UPPER ? null : new AtmBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockPos lower = lowerPos(pos, state);
        AtmBlockEntity atm = (AtmBlockEntity) world.getBlockEntity(lower);
        if (atm == null) return ActionResult.PASS;
        ItemStack held = player.getStackInHand(hand);
        if (held.isOf(CityEconomyItems.WRENCH)) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override public void writeScreenOpeningData(ServerPlayerEntity p, PacketByteBuf buf) {
                    buf.writeBlockPos(pos);
                    buf.writeBoolean(true);
                    buf.writeBoolean(atm.enabled);
                    buf.writeInt(atm.withdrawAmount);
                }
                @Override public Text getDisplayName() { return Text.translatable("screen.cityec.atm_config"); }
                @Override public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) { return atm.createConfigHandler(syncId, inv); }
            });
            return ActionResult.CONSUME;
        }
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override public void writeScreenOpeningData(ServerPlayerEntity p, PacketByteBuf buf) {
                buf.writeBlockPos(lower);
                buf.writeInt(atm.withdrawAmount);
                buf.writeBoolean(atm.enabled);
            }
            @Override public Text getDisplayName() { return Text.translatable("screen.cityec.atm"); }
            @Override public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) { return atm.createAtmHandler(syncId, inv); }
        });
        return ActionResult.CONSUME;
    }
}
