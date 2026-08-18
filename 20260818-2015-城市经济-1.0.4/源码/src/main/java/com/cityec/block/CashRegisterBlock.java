package com.cityec.block;

import com.cityec.block.entity.CashRegisterBlockEntity;
import com.cityec.item.CityEconomyItems;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CashRegisterBlock extends Block implements BlockEntityProvider {
    public CashRegisterBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(Properties.HORIZONTAL_FACING, net.minecraft.util.math.Direction.NORTH)
                .with(Properties.POWERED, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, Properties.POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(Properties.POWERED, true);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CashRegisterBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        CashRegisterBlockEntity register = (CashRegisterBlockEntity) world.getBlockEntity(pos);
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
                @Override public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) {
                    return register.createConfigHandler(syncId, inv);
                }
            });
            return ActionResult.CONSUME;
        }
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override public void writeScreenOpeningData(ServerPlayerEntity p, PacketByteBuf buf) {
                buf.writeBlockPos(pos);
                buf.writeInt(register.price);
                buf.writeBoolean(register.enabled);
            }
            @Override public Text getDisplayName() { return Text.translatable("screen.cityec.register"); }
            @Override public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) {
                return register.createRegisterHandler(syncId, inv);
            }
        });
        return ActionResult.CONSUME;
    }
}
