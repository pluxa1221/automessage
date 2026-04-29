package org.nevetime.autoMessage;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.nevetime.autoMessage.command.AutoMessageCommand;
import org.nevetime.autoMessage.command.AutoMessageTabComplete;

import java.util.ArrayList;
import java.util.List;

public class AutoMessage extends JavaPlugin {

    // Utils
    private final MiniMessage mm = MiniMessage.miniMessage();

    // Managers
    private final BroadcastManager broadcastManager = new BroadcastManager(this);

    // Variables
    private String prefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPluginConfig();

        getCommand("automessage").setExecutor(new AutoMessageCommand(this));
        getCommand("automessage").setTabCompleter(new AutoMessageTabComplete());
    }

    @Override
    public void onDisable() {
        broadcastManager.stop();
    }

    public void reloadPluginConfig() {
        reloadConfig();
        prefix = getConfig().getString("prefix", "<gray>[AutoMessage] ");
        broadcastManager.restart();
    }

    // Getters

    public BroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

    public MiniMessage getMiniMessage() {
        return mm;
    }

    public String getPrefix() {
        return prefix;
    }
}
