package com.cityec;

import com.cityec.block.CityEconomyBlocks;
import com.cityec.block.entity.CityEconomyBlockEntities;
import com.cityec.economy.EconomyUtil;
import com.cityec.item.CityEconomyItems;
import com.cityec.screen.CityEconomyScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public final class CityEconomyMod implements ModInitializer {
    public static final String MOD_ID = "cityec";

    @Override
    public void onInitialize() {
        CityEconomyItems.register();
        CityEconomyBlocks.register();
        CityEconomyBlockEntities.register();
        CityEconomyScreenHandlers.register();

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
