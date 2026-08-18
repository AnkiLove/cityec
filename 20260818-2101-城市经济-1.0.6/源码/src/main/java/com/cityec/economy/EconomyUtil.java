package com.cityec.economy;

import com.cityec.item.CashItem;
import com.cityec.item.CityEconomyItems;
import com.cityec.item.ValueCardItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EconomyUtil {
    public static final String BALANCE_KEY = "cityec_balance";
    private static final List<Item> DENOMINATIONS = List.of(
            CityEconomyItems.CASH_100, CityEconomyItems.CASH_50, CityEconomyItems.CASH_20,
            CityEconomyItems.CASH_10, CityEconomyItems.CASH_5, CityEconomyItems.CASH_1);

    private EconomyUtil() {}

    public static int getCardBalance(ItemStack stack) {
        if (!(stack.getItem() instanceof ValueCardItem)) return 0;
        return stack.getOrCreateNbt().getInt(BALANCE_KEY);
    }

    public static void setCardBalance(ItemStack stack, int balance) {
        if (stack.getItem() instanceof ValueCardItem) stack.getOrCreateNbt().putInt(BALANCE_KEY, Math.max(0, balance));
    }

    public static boolean chargeCard(ItemStack stack, int amount) {
        int balance = getCardBalance(stack);
        if (balance < amount) return false;
        setCardBalance(stack, balance - amount);
        return true;
    }

    public static Optional<ItemStack> findFirstCard(PlayerEntity player) {
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof ValueCardItem) return Optional.of(stack);
        }
        if (player.getOffHandStack().getItem() instanceof ValueCardItem) return Optional.of(player.getOffHandStack());
        return Optional.empty();
    }

    public static int cashValue(ItemStack stack) {
        return stack.getItem() instanceof CashItem cash ? cash.value() : 0;
    }

    public static void giveCash(PlayerEntity player, int amount) {
        int remaining = Math.max(0, amount);
        for (Item denomination : DENOMINATIONS) {
            int value = cashValue(new ItemStack(denomination));
            int count = remaining / value;
            remaining %= value;
            while (count-- > 0) {
                ItemStack stack = new ItemStack(denomination, 1);
                if (!player.getInventory().insertStack(stack)) player.dropItem(stack, false);
            }
        }
    }

    public static boolean removeCash(PlayerEntity player, int amount) {
        int remaining = amount;
        for (Item denomination : DENOMINATIONS) {
            int value = cashValue(new ItemStack(denomination));
            int available = 0;
            for (ItemStack stack : player.getInventory().main) {
                if (stack.isOf(denomination)) available += stack.getCount();
            }
            int take = Math.min(available, remaining / value);
            if (take > 0) removeItem(player, denomination, take);
            remaining -= take * value;
        }
        return remaining == 0;
    }

    /** Returns the inserted total, or -1 when the inventory cannot cover the requested amount. */
    public static int payCash(PlayerEntity player, int amount) {
        int remaining = amount;
        List<CashTake> selected = new ArrayList<>();
        for (Item denomination : DENOMINATIONS) {
            int value = cashValue(new ItemStack(denomination));
            int available = countItem(player, denomination);
            int take = Math.min(available, remaining / value);
            if (take > 0) {
                selected.add(new CashTake(denomination, take));
                remaining -= take * value;
            }
        }
        if (remaining > 0) {
            for (int index = DENOMINATIONS.size() - 1; index >= 0; index--) {
                Item denomination = DENOMINATIONS.get(index);
                int alreadySelected = selected.stream().filter(take -> take.item() == denomination).mapToInt(CashTake::count).sum();
                if (countItem(player, denomination) > alreadySelected) {
                    selected.add(new CashTake(denomination, 1));
                    remaining -= cashValue(new ItemStack(denomination));
                    break;
                }
            }
        }
        if (remaining > 0) return -1;
        int inserted = amount - remaining;
        for (CashTake take : selected) removeItem(player, take.item(), take.count());
        return inserted;
    }

    private static void removeItem(PlayerEntity player, Item item, int count) {
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isOf(item)) continue;
            int removed = Math.min(count, stack.getCount());
            stack.decrement(removed);
            count -= removed;
            if (count == 0) return;
        }
    }

    private static int countItem(PlayerEntity player, Item item) {
        int count = 0;
        for (ItemStack stack : player.getInventory().main) if (stack.isOf(item)) count += stack.getCount();
        return count;
    }

    public static void depositOneCash(PlayerEntity player, ItemStack held) {
        int value = cashValue(held);
        if (value <= 0) return;
        Optional<ItemStack> card = findFirstCard(player);
        if (card.isEmpty()) {
            player.sendMessage(Text.translatable("cityec.message.need_card"), true);
            return;
        }
        depositOneCashToCard(player, held, card.get());
    }

    public static void depositOneCashToCard(PlayerEntity player, ItemStack held, ItemStack card) {
        int value = cashValue(held);
        if (value <= 0 || !(card.getItem() instanceof ValueCardItem)) return;
        held.decrement(1);
        setCardBalance(card, getCardBalance(card) + value);
        player.sendMessage(Text.translatable("cityec.message.deposit", value, getCardBalance(card)), true);
    }

    private record CashTake(Item item, int count) {}
}
