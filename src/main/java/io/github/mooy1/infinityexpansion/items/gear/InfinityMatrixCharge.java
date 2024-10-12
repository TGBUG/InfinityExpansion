package io.github.mooy1.infinityexpansion.items.gear;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;

import io.github.thebusybiscuit.slimefun4.utils.ChargeUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

public interface InfinityMatrixCharge {
    float getMaxItemCharge(ItemStack item);

    default float getItemCharge(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            throw new IllegalArgumentException("Cannot get Item charge for null or AIR");
        }

        return ChargeUtils.getCharge(item.getItemMeta());
    }


    default void setItemCharge(ItemStack item, float charge) {
        if (item == null || item.getType() == Material.AIR) {
            throw new IllegalArgumentException("Cannot set Item charge for null or AIR");
        }

        float maximum = getMaxItemCharge(item);

        if (charge < 0 || charge > maximum) {
            throw new IllegalArgumentException("Charge must be between zero and " + maximum + ".");
        }

        ItemMeta meta = item.getItemMeta();
        ChargeUtils.setCharge(meta, charge, maximum);
        item.setItemMeta(meta);
    }


    @Nonnull
    ItemUseHandler getItemHandler();
}
