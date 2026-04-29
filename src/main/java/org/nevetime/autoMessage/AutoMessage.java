package org.nevetime.autoMessage;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.nevetime.autoMessage.command.AutoMessageCommand;
import org.nevetime.autoMessage.command.AutoMessageTabComplete;

import java.util.ArrayList;
import java.util.List;

public class AutoMessage extends JavaPlugin {

    private final MiniMessage mm = MiniMessage.miniMessage();
    private final AutoMessageUtils utils = new AutoMessageUtils();
    private String prefix;

    // Events
    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPluginConfig();

        getCommand("automessage").setExecutor(new AutoMessageCommand(this));
        getCommand("automessage").setTabCompleter(new AutoMessageTabComplete());
    }

    @Override
    public void onDisable() {
        utils.cancelTasks();
    }

    // Internal methods
    public void reloadPluginConfig() {
        reloadConfig();
        prefix = getConfig().getString("prefix", "<gray>[AutoMessage] ");
        restartBroadcasts();
    }

    // Getters
    public MiniMessage getMiniMessage() {
        return mm;
    }

    public String getPrefix() {
        return prefix;
    }
}
