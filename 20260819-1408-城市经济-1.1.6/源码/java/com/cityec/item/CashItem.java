package com.cityec.item;

import net.minecraft.item.Item;

public class CashItem extends Item {
    private final int value;

    public CashItem(Settings settings, int value) {
        super(settings);
        this.value = value;
    }

    public int value() {
        return value;
    }
}
