package org.nevetime.autoMessage.command;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.nevetime.autoMessage.AutoMessage;

public class AutoMessageCommand implements CommandExecutor {
    private final AutoMessage plugin;
    private final MiniMessage mm;
    private final String prefix;

    public AutoMessageCommand(AutoMessage plugin) {
        this.plugin = plugin;
        this.mm = plugin.getMiniMessage();
        this.prefix = plugin.getPrefix();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("automessage.admin")) {
            sender.sendMessage(this.plugin.getConfig().getString("command-messages.no-permission", "&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(this.plugin.getConfig().getString("command-messages.unknown-arg", "&cUnknown argument. Please specify a valid subcommand."));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                this.plugin.reloadPluginConfig();
                sender.sendMessage(this.plugin.getConfig().getString("command-messages.reload-success", "&aConfiguration reloaded successfully."));
            }
            case "restart" -> {
                this.plugin.restartBroadcasts();
                sender.sendMessage(plugin.getConfig().getString("command-messages.restart-success", "&aAutoMessages restarted successfully."));
            }
            default -> sender.sendMessage(this.plugin.getConfig().getString("command-messages.unknown-arg", "&cUnknown argument. Please specify a valid subcommand."));
        }
        return true;
    }

    private void sendMessage(CommandSender sender, String raw) {
        if (raw == null) return;

        String parsed = null;

        if (sender instanceof Player) {
            parsed = PlaceholderAPI.setPlaceholders(
                    (Player) sender, raw
            );
        }

        if (parsed != null) {
            sender.sendMessage(this.mm.deserialize(this.prefix).append(mm.deserialize(parsed)));
        } else {
            sender.sendMessage(this.mm.deserialize(this.prefix).append(mm.deserialize(raw)));
        }
    }
}