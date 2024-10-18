package io.github.mooy1.infinityexpansion.items.gear;

import io.github.mooy1.infinityexpansion.InfinityExpansion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChargableInfinityMatrix extends InfinityMatrix implements Rechargeable {
    private final float capacity;
    private final byte[] lock=new byte[0];
    private final HashMap<Player, Location> playerLocations = new HashMap<>();
    public ChargableInfinityMatrix(ItemGroup category, SlimefunItemStack item, RecipeType type, ItemStack[] recipe,float capacity){
        super(category, item, type, recipe);
        this.capacity = capacity;
        this.MatrixCharge.runTaskTimer(InfinityExpansion.instance(), 200, 20);
        this.setItemCharge(item,0);
    }
    @Override
    public float getMaxItemCharge(ItemStack item) {
        return capacity;
    }
    public void disableFlight(Player p) {
        super.disableFlight(p);
        synchronized (lock) {
            playerLocations.remove(p);
        }
    }

    public void enableFlight(Player p) {
       super.enableFlight(p);
       synchronized (lock) {
           playerLocations.put(p, p.getLocation());
       }
    }
    public boolean removeMatrixCharge(Player p, float charge, ItemStack item) {
        if (charge == 0 || charge > 100 && !(getItemCharge(item) == 0)) {
            return true;
        }
        else {
            if (removeItemCharge(item, charge)) {
                return true;
            }
            else {
                return false;
            }
        }
    }
    public BukkitRunnable MatrixCharge = new BukkitRunnable() {
        @Override
        public void run() {
            synchronized (lock){
                Iterator<Map.Entry<Player,Location>> iterator=playerLocations.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<Player,Location> entry=iterator.next();
                    Player player=entry.getKey();
                    Location location=entry.getValue();
                    Location playerLocation=player.getLocation();
                    //在线且会飞
                    if(player.isOnline() && player.getAllowFlight()){
                        if(location.getWorld()!=playerLocation.getWorld()){
                            //玩家切换世界，关闭
                            ChargableInfinityMatrix.super.disableFlight(player);
                            iterator.remove();
                        }else{
                            //防止玩家开着飞行器走路
                            if(player.isFlying()){
                            //计算玩家移动距离
                                double distance=location.distance(playerLocation);
                                float charge = (float) distance* 4;
                                boolean find=false;
                                boolean removed = false;
                                for (ItemStack item : player.getInventory().getContents()) {
                                    if (item != null && item.getType() == Material.NETHER_STAR) {
                                        find=true;
                                        if (removeMatrixCharge(player, charge, item)) {
                                            removed=true;
                                            break;
                                        }else {
                                            removed=false;
                                        }
                                    }
                                }
                                if(!removed){
                                    //找不到飞行器,关闭
                                    if(find){
                                        player.sendMessage(ChatColor.RED + "所有飞行器电量不足!");
                                    }
                                    ChargableInfinityMatrix.super.disableFlight(player);
                                    iterator.remove();
                                }else{
                                    //刷新当前位置
                                    entry.setValue(playerLocation);
                                }
                            }
                        }
                    }else{
                        iterator.remove();
                    }
                }
            }
        }
    };
}
