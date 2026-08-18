package com.cityec.screen;

import com.cityec.CityEconomyMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.util.Identifier;

public final class CityEconomyScreenHandlers {
    public static final ScreenHandlerType<ConfigScreenHandler> CONFIG = Registry.register(
            Registries.SCREEN_HANDLER, new Identifier(CityEconomyMod.MOD_ID, "config"),
            new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> new ConfigScreenHandler(syncId, inventory, buf)));
    public static final ScreenHandlerType<AtmScreenHandler> ATM = Registry.register(
            Registries.SCREEN_HANDLER, new Identifier(CityEconomyMod.MOD_ID, "atm"),
            new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> new AtmScreenHandler(syncId, inventory, buf)));
    public static final ScreenHandlerType<RegisterScreenHandler> REGISTER = Registry.register(
            Registries.SCREEN_HANDLER, new Identifier(CityEconomyMod.MOD_ID, "register"),
            new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> new RegisterScreenHandler(syncId, inventory, buf)));
    public static final ScreenHandlerType<CardScreenHandler> CARD = Registry.register(
            Registries.SCREEN_HANDLER, new Identifier(CityEconomyMod.MOD_ID, "card"),
            new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> new CardScreenHandler(syncId, inventory, buf)));

    public static void register() {}
}
