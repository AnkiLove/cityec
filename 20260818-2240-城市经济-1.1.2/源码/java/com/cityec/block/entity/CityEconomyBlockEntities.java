package com.cityec.block.entity;

import com.cityec.CityEconomyMod;
import com.cityec.block.CityEconomyBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class CityEconomyBlockEntities {
    public static final BlockEntityType<AtmBlockEntity> ATM = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(CityEconomyMod.MOD_ID, "auto_teller_machine"),
            BlockEntityType.Builder.create(AtmBlockEntity::new, CityEconomyBlocks.ATM).build(null));
    public static final BlockEntityType<CashRegisterBlockEntity> CASH_REGISTER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(CityEconomyMod.MOD_ID, "cash_register"),
            BlockEntityType.Builder.create(CashRegisterBlockEntity::new, CityEconomyBlocks.CASH_REGISTER).build(null));

    public static void register() {}
}
