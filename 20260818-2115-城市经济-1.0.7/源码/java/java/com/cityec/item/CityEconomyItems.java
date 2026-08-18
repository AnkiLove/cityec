package com.cityec.item;

import com.cityec.CityEconomyMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class CityEconomyItems {
    public static final Item CASH_1 = register("1yuan", new CashItem(new Item.Settings().maxCount(50), 1));
    public static final Item CASH_5 = register("5yuan", new CashItem(new Item.Settings().maxCount(50), 5));
    public static final Item CASH_10 = register("10yuan", new CashItem(new Item.Settings().maxCount(50), 10));
    public static final Item CASH_20 = register("20yuan", new CashItem(new Item.Settings().maxCount(50), 20));
    public static final Item CASH_50 = register("50yuan", new CashItem(new Item.Settings().maxCount(50), 50));
    public static final Item CASH_100 = register("100yuan", new CashItem(new Item.Settings().maxCount(50), 100));
    public static final Item VALUE_CARD = register("value_card", new ValueCardItem(new Item.Settings().maxCount(1)));
    public static final Item WRENCH = register("wrench", new Item(new Item.Settings().maxCount(1)));
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.cityec.main"))
            .icon(() -> new ItemStack(VALUE_CARD))
            .entries((context, entries) -> {
                entries.add(CASH_1);
                entries.add(CASH_5);
                entries.add(CASH_10);
                entries.add(CASH_20);
                entries.add(CASH_50);
                entries.add(CASH_100);
                entries.add(VALUE_CARD);
                entries.add(WRENCH);
                entries.add(com.cityec.block.CityEconomyBlocks.ATM);
                entries.add(com.cityec.block.CityEconomyBlocks.CASH_REGISTER);
            })
            .build();

    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CityEconomyMod.MOD_ID, id), item);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier(CityEconomyMod.MOD_ID, "main"), ITEM_GROUP);
    }
}
