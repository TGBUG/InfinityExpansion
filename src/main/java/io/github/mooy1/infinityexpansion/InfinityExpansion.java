package io.github.mooy1.infinityexpansion;

import java.io.File;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import io.github.mooy1.infinityexpansion.items.blocks.InfinityWorkbench;
import io.github.mooy1.infinityexpansion.items.gear.InfinityMatrix;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import io.github.mooy1.infinityexpansion.categories.Groups;
import io.github.mooy1.infinityexpansion.commands.GiveRecipe;
import io.github.mooy1.infinityexpansion.commands.PrintItem;
import io.github.mooy1.infinityexpansion.commands.SetData;
import io.github.mooy1.infinityexpansion.commands.UpgradeItem;
import io.github.mooy1.infinityexpansion.items.Researches;
import io.github.mooy1.infinityexpansion.items.SlimefunExtension;
import io.github.mooy1.infinityexpansion.items.blocks.Blocks;
import io.github.mooy1.infinityexpansion.items.gear.Gear;
import io.github.mooy1.infinityexpansion.items.generators.Generators;
import io.github.mooy1.infinityexpansion.items.machines.Machines;
import io.github.mooy1.infinityexpansion.items.materials.Materials;
import io.github.mooy1.infinityexpansion.items.mobdata.MobData;
import io.github.mooy1.infinityexpansion.items.quarries.Quarries;
import io.github.mooy1.infinityexpansion.items.storage.Storage;
import io.github.mooy1.infinityexpansion.items.storage.StorageSaveFix;
import io.github.mooy1.infinitylib.common.Scheduler;
import io.github.mooy1.infinitylib.core.AbstractAddon;
import io.github.mooy1.infinitylib.metrics.bukkit.Metrics;
import io.github.mooy1.infinitylib.metrics.charts.SimplePie;

import net.guizhanss.slimefun4.utils.WikiUtils;

import static io.github.mooy1.infinityexpansion.items.gear.Gear.INFINITY_MATRIX;


public final class InfinityExpansion extends AbstractAddon {

    public InfinityExpansion(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file,
                "SlimefunGuguProject", "InfinityExpansion", "master", "auto-update");
    }

    public InfinityExpansion() {
        super("SlimefunGuguProject", "InfinityExpansion", "master", "auto-update");
        StorageSaveFix.fixStuff(getLogger());
    }


    @Override
    protected void enable() {
        Metrics metrics = new Metrics(this, 8991);
        boolean enableAutoUpdate = getConfig().getBoolean("auto-update");
        String autoUpdates = String.valueOf(enableAutoUpdate);
        metrics.addCustomChart(new SimplePie("auto_updates", () -> autoUpdates));


        if (enableAutoUpdate && getDescription().getVersion().startsWith("Build")) {
            GuizhanUpdater.start(this, getFile(), "SlimefunGuguProject", "InfinityExpansion", "master");
        }

        Plugin lx = getServer().getPluginManager().getPlugin("LiteXpansion");
        if (lx != null && lx.getConfig().getBoolean("options.nerf-other-addons")) {
            Scheduler.run(() -> log(Level.WARNING,
                    "########################################################",
                    "LiteXpansion 对本附属插件中的发电机效率进行了削弱",
                    "你可以在 LiteXpansion 的配置文件中更改设置",
                    "在 'options' 中设置 'nerf-other-addons' 为false",
                    "########################################################"
            ));
        }

        getAddonCommand()
                .addSub(new GiveRecipe())
                .addSub(new SetData())
                .addSub(new PrintItem())
                .addSub(new UpgradeItem());

        Groups.setup(this);
        MobData.setup(this);
        Materials.setup(this);
        Machines.setup(this);
        Quarries.setup(this);
        Gear.setup(this);
        Blocks.setup(this);
        Storage.setup(this);
        Generators.setup(this);
        SlimefunExtension.setup(this);

        WikiUtils.setupJson(this);

        if (getConfig().getBoolean("balance-options.enable-researches")) {
            Researches.setup();
        }

        InfinityMatrix MatrixCharge = new InfinityMatrix(Groups.INFINITY_CHEAT, INFINITY_MATRIX, InfinityWorkbench.TYPE, new ItemStack[] {
            Materials.INFINITE_INGOT, null, Materials.INFINITE_INGOT, Materials.INFINITE_INGOT, null, Materials.INFINITE_INGOT,
            Generators.INFINITY_REACTOR, Materials.INFINITE_CIRCUIT, Materials.INFINITE_CIRCUIT, Materials.INFINITE_CIRCUIT, Materials.INFINITE_CIRCUIT, Generators.INFINITY_REACTOR,
            Materials.VOID_INGOT, Materials.INFINITE_CIRCUIT, SlimefunItems.SOULBOUND_ELYTRA, SlimefunItems.SOULBOUND_ELYTRA, Materials.INFINITE_CIRCUIT, Materials.VOID_INGOT,
            Materials.VOID_INGOT, Materials.INFINITE_CIRCUIT, Generators.INFINITE_PANEL, Generators.INFINITE_PANEL, Materials.INFINITE_CIRCUIT, Materials.VOID_INGOT,
            Generators.INFINITY_REACTOR, Materials.INFINITE_CIRCUIT, Materials.INFINITE_CIRCUIT, Materials.INFINITE_CIRCUIT, Materials.INFINITE_CIRCUIT, Generators.INFINITY_REACTOR,
            Materials.INFINITE_INGOT, null, Materials.INFINITE_INGOT, Materials.INFINITE_INGOT, null, Materials.INFINITE_INGOT
        }, 8192);
        MatrixCharge.MatrixCharge.runTaskTimer(this, 20, 20);
    }

    @Override
    public void disable() {
    }

    @Nonnull
    public String getWikiURL() {
        return "https://slimefun-addons-wiki.guizhanss.cn/infinity-expansion/{0}";
    }



}
