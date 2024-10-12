package io.github.mooy1.infinityexpansion.commands;

import java.util.List;

import javax.annotation.Nonnull;

import com.xzavier0722.mc.plugin.slimefun4.storage.callback.IAsyncReadCallback;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.mooy1.infinityexpansion.items.storage.StorageUnit;
import io.github.mooy1.infinitylib.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

public final class SetData extends SubCommand {

    public SetData() {
        super("setdata", "设置你指向的 Slimefun 方块的数据", "infinityexpansion.setdata");
    }

    @Override
    protected void execute(@Nonnull CommandSender commandSender, @Nonnull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("只有玩家可以使用该指令!");
            return;
        }

        if (strings.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "你必须指定键和值!");
            return;
        }

        Player p = (Player) commandSender;

        Block target = p.getTargetBlockExact(8, FluidCollisionMode.NEVER);

        if (target == null || target.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "你必须指着一个方块才能执行该指令!");
            return;
        }

        SlimefunBlockData blockData = StorageCacheUtils.getBlock(target.getLocation());
        if (blockData == null) {
            p.sendMessage(ChatColor.RED + "你必须指着一个 Slimefun 方块才能执行该指令!");
            return;
        }

        if (blockData.isDataLoaded()) {
            setData(blockData, p, strings, target);
        } else {
            Slimefun.getDatabaseManager().getBlockDataController().loadBlockDataAsync(
                blockData,
                new IAsyncReadCallback<SlimefunBlockData>() {
                    @Override
                    public boolean runOnMainThread() {
                        return true;
                    }

                    @Override
                    public void onResult(SlimefunBlockData result) {
                        setData(blockData, p, strings, target);
                    }
                }
            );
        }
    }

    private void setData(SlimefunBlockData blockData, Player p, String[] strings, Block target) {
        if (strings[1].equals("\\remove")) {
            p.sendMessage(ChatColor.GREEN + "已移除 " + blockData.getSfId() + "中 '" + strings[1] + "' 的值");
            blockData.removeData(strings[1]);
        }
        else {
            p.sendMessage(ChatColor.GREEN + "已设置 " + blockData.getSfId() + "中 '" + strings[0] + "' 的值为 '" + strings[1] + "'");
            blockData.setData(strings[0], strings[1]);
        }

        SlimefunItem unit = SlimefunItem.getById(blockData.getSfId());
        if (unit instanceof StorageUnit) {
            ((StorageUnit) unit).reloadCache(target);
        }
    }

    @Override
    protected void complete(@Nonnull CommandSender commandSender, @Nonnull String[] strings, @Nonnull List<String> list) {
        if (!(commandSender instanceof Player)) {
            return;
        }

        Player p = (Player) commandSender;

        Block target = p.getTargetBlockExact(8, FluidCollisionMode.NEVER);

        if (target == null || target.getType() == Material.AIR) {
            return;
        }

        SlimefunBlockData blockData = StorageCacheUtils.getBlock(target.getLocation());
        if (blockData == null) {
            return;
        }

        if (!blockData.isDataLoaded()) {
            StorageCacheUtils.requestLoad(blockData);
            return;
        }

        if (strings.length == 1) {
            if (StorageCacheUtils.hasBlock(target.getLocation())) {
                list.addAll(blockData.getDataKeys());
            }
        }
        else if (strings.length == 2) {
            String current = blockData.getData(strings[1]);
            if (current != null) {
                list.add(current);
                list.add("\\remove");
            }
        }
    }

}
