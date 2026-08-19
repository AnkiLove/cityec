package com.cityec;

import com.cityec.block.CityEconomyBlocks;
import com.cityec.block.entity.AtmBlockEntity;
import com.cityec.block.entity.CashRegisterBlockEntity;
import com.cityec.block.entity.CityEconomyBlockEntities;
import com.cityec.economy.EconomyUtil;
import com.cityec.item.CityEconomyItems;
import com.cityec.screen.CityEconomyScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class CityEconomyMod implements ModInitializer {
    public static final String MOD_ID = "cityec";
    public static final Identifier CONFIG_PACKET = new Identifier(MOD_ID, "config_amount");

    @Override
    public void onInitialize() {
        CityEconomyItems.register();
        CityEconomyBlocks.register();
        CityEconomyBlockEntities.register();
        CityEconomyScreenHandlers.register();

        ServerPlayNetworking.registerGlobalReceiver(CONFIG_PACKET, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            boolean atm = buf.readBoolean();
            boolean enabled = buf.readBoolean();
            int value = Math.max(1, Math.min(1_000_000, buf.readInt()));
            server.execute(() -> {
                if (player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) return;
                if (atm) {
                    if (player.getWorld().getBlockEntity(pos) instanceof AtmBlockEntity entity) entity.applyConfig(enabled, value);
                } else if (player.getWorld().getBlockEntity(pos) instanceof CashRegisterBlockEntity entity) {
                    entity.applyConfig(enabled, value);
                }
            });
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("cityec")
                        .then(CommandManager.literal("balance")
                                .executes(context -> {
                                    int balance = EconomyUtil.findFirstCard(context.getSource().getPlayer()).map(EconomyUtil::getCardBalance).orElse(0);
                                    context.getSource().sendFeedback(() -> Text.translatable("cityec.command.balance", balance), false);
                                    return balance;
                                }))
                        .then(CommandManager.literal("give")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("amount", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int amount = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "amount");
                                            EconomyUtil.giveCash(context.getSource().getPlayer(), amount);
                                            context.getSource().sendFeedback(() -> Text.translatable("cityec.command.give", amount), true);
                                            return amount;
                                        })))
        ));
    }
}
