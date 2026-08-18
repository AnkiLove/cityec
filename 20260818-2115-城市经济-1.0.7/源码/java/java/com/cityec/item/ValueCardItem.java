package com.cityec.item;

import com.cityec.economy.EconomyUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import com.cityec.screen.CardScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ValueCardItem extends Item {
    public ValueCardItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, net.minecraft.entity.player.PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack);
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override public void writeScreenOpeningData(ServerPlayerEntity p, PacketByteBuf buf) { buf.writeInt(EconomyUtil.getCardBalance(stack)); }
            @Override public Text getDisplayName() { return Text.translatable("screen.cityec.card"); }
            @Override public ScreenHandler createMenu(int syncId, PlayerInventory inventory, net.minecraft.entity.player.PlayerEntity p) {
                return new CardScreenHandler(syncId, inventory, EconomyUtil.getCardBalance(stack));
            }
        });
        return TypedActionResult.consume(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.cityec.value_card.balance", EconomyUtil.getCardBalance(stack)));
        tooltip.add(Text.translatable("item.cityec.value_card.hint"));
    }
}
