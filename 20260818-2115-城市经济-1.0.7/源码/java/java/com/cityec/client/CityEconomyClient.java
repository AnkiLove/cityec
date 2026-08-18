package com.cityec.client;

import com.cityec.screen.CityEconomyScreenHandlers;
import com.cityec.screen.ConfigScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public final class CityEconomyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(CityEconomyScreenHandlers.CONFIG, ConfigScreen::new);
        HandledScreens.register(CityEconomyScreenHandlers.ATM, AtmScreen::new);
        HandledScreens.register(CityEconomyScreenHandlers.REGISTER, RegisterScreen::new);
        HandledScreens.register(CityEconomyScreenHandlers.CARD, CardScreen::new);
    }
}
