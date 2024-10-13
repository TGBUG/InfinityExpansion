package io.github.mooy1.infinityexpansion.items.gear;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.mooy1.infinitylib.common.Events;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Soulbound;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;


public final class InfinityMatrix extends SimpleSlimefunItem<ItemUseHandler> implements Listener, Soulbound, NotPlaceable, Rechargeable {

    private final float capacity;
    private final HashMap<Player, Location> lastLocations = new HashMap<>();

    public InfinityMatrix(ItemGroup category, SlimefunItemStack item, RecipeType type, ItemStack[] recipe, float capacity) {
        super(category, item, type, recipe);
        this.capacity = capacity;
        Events.registerListener(this);
    }

    private static void disableFlight(Player p) {
        p.sendMessage(ChatColor.RED + "无尽飞行已禁用!");
        p.setAllowFlight(false);
    }

    private static void enableFlight(Player p) {
        p.sendMessage(ChatColor.GREEN + "无尽飞行已启用!");
        p.setAllowFlight(true);
    }


    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            ItemStack item = e.getItem();
            if (!item.hasItemMeta()) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (!meta.hasLore()) {
                return;
            }
            List<String> lore = meta.getLore();

            Player p = e.getPlayer();
            p.sendMessage(String.valueOf(item));

            Iterator<String> iterator = lore.listIterator();

            while (iterator.hasNext()) {
                String line = iterator.next();

                if (ChatColor.stripColor(line).contains("UUID: ")) {
                    String uuid = ChatColor.stripColor(line).substring(6);

                    if (!p.getUniqueId().toString().equals(uuid)) {
                        p.sendMessage(ChatColor.YELLOW + "你不是飞行器的主人!");
                        return;
                    }

                    if (p.isSneaking()) { //remove owner
                        iterator.remove();
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        p.sendMessage(ChatColor.GOLD + "已解除绑定飞行器!");
                        disableFlight(p);

                    } else if (p.getAllowFlight()) {
                        disableFlight(p);
                    } else {
                        enableFlight(p);
                    }

                    return;
                }
            }


            lore.add(ChatColor.GREEN + "UUID: " + p.getUniqueId());
            meta.setLore(lore);
            item.setItemMeta(meta);
            p.sendMessage(ChatColor.GOLD + "已绑定飞行器!");
            enableFlight(p);
        };
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location lastLocation = lastLocations.get(p);
        Location currentLocation = p.getLocation();

        if (lastLocation == null || lastLocation.distance(currentLocation) >= 1) {
            lastLocations.put(p, currentLocation);

            ItemStack item = (getItem());
            p.sendMessage("test");
            if (p.getAllowFlight()) {
                p.sendMessage(String.valueOf(item));
                p.sendMessage(String.valueOf(getItemCharge(item)));
                p.sendMessage(String.valueOf(getMaxItemCharge(item)));
                if (removeItemCharge(item, (float) lastLocation.distance(currentLocation))) {
                    p.sendMessage(String.valueOf(getItemCharge(item)));
                    if (getItemCharge(item) <= 128) {
                        p.sendMessage(ChatColor.RED + "飞行器电量低!");
                    }
                } else {
                    disableFlight(p);
                    p.sendMessage(ChatColor.RED + "飞行器电量不足!");
                }
            }
        }
    }


    @Override
    public float getMaxItemCharge(ItemStack item) {
        return capacity;
    }


}
