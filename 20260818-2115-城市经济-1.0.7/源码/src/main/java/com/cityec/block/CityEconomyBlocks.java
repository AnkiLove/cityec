package com.cityec.block;

import com.cityec.CityEconomyMod;
import com.cityec.block.entity.AtmBlockEntity;
import com.cityec.block.entity.CashRegisterBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.item.BlockItem;

public final class CityEconomyBlocks {
    public static final Block ATM = register("auto_teller_machine", new AtmBlock(Block.Settings.create().strength(3.5f).sounds(BlockSoundGroup.METAL).requiresTool()));
    public static final Block CASH_REGISTER = register("cash_register", new CashRegisterBlock(Block.Settings.create().strength(2.5f).sounds(BlockSoundGroup.METAL).requiresTool()));

    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(CityEconomyMod.MOD_ID, id), block);
    }

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(CityEconomyMod.MOD_ID, "auto_teller_machine"), new BlockItem(ATM, new net.minecraft.item.Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier(CityEconomyMod.MOD_ID, "cash_register"), new BlockItem(CASH_REGISTER, new net.minecraft.item.Item.Settings()));
    }
}
