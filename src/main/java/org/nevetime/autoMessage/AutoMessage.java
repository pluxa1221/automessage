package org.nevetime.autoMessage;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoMessage extends JavaPlugin {

    private final MiniMessage mm = MiniMessage.miniMessage();
    private final List<BukkitTask> tasks = new ArrayList<>();
    private String prefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPluginConfig();
    }

    @Override
    public void onDisable() {
        cancelTasks();
    }

    private void reloadPluginConfig() {
        reloadConfig();
        prefix = getConfig().getString("prefix", "<gray>[AutoMessage] ");
        restartBroadcasts();
    }

    private void restartBroadcasts() {
        cancelTasks();
        loadBroadcasts();
    }

    private void cancelTasks() {
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
    }

    private String stripInteractiveTags(String text) {
        if (text == null) return "";

        return text
                .replaceAll("(?i)<click:[^>]*>","")
                .replaceAll("(?i)</click>","")
                .replaceAll("(?i)<hover:[^>]*>","")
                .replaceAll("(?i)</hover>","")
                ;
    }

    private void loadBroadcasts() {
        List<Map<?, ?>> list = getConfig().getMapList("messages");
        if (list.isEmpty()) {
            getLogger().severe("Не удалось найти или загрузить секцию messages!");
        } else {
            getLogger().info("Загружено " + list.size() + " сообщений из конфига.");
        }

        for (Map<?, ?> map : list) {
            String type = (String) map.get("type");
            int period = (map.get("period") instanceof Number n) ? n.intValue() : 300;

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        switch (type) {
                            case "actionbar" -> {
                                String raw = map.get("text").toString();
                                String stripped = stripInteractiveTags(raw);
                                String parsed = PlaceholderAPI.setPlaceholders(player, stripped);
                                Component comp = mm.deserialize(parsed);
                                player.sendActionBar(comp);
                            }
                            case "title" -> {
                                String rawTitle = map.get("title").toString();
                                String rawSub = map.get("subtitle").toString();

                                String strippedTitle = stripInteractiveTags(rawTitle);
                                String strippedSub = stripInteractiveTags(rawSub);

                                String parsedTitle = PlaceholderAPI.setPlaceholders(player, strippedTitle);
                                String parsedSub = PlaceholderAPI.setPlaceholders(player, strippedSub);

                                Component compTitle = mm.deserialize(parsedTitle);
                                Component compSub = mm.deserialize(parsedSub);

                                player.showTitle(Title.title(compTitle, compSub));
                            }
                            default -> {
                                Object raw = map.get("text");
                                String finalMessage;

                                if (raw instanceof List<?> list) {
                                    finalMessage = list.stream()
                                            .map(Object::toString)
                                            .collect(Collectors.joining("<newline>"));
                                } else {
                                    finalMessage = String.valueOf(raw);
                                }
                                String parsed = PlaceholderAPI.setPlaceholders(player, finalMessage);

                                Component comp = mm.deserialize(parsed);

                                player.sendMessage(comp);
                            }

                        }
                    }
                }
            }.runTaskTimer(this, period, period);

            tasks.add(task);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("automessage.admin")) {
            sendMessage(sender, getConfig().getString("command-messages.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendMessage(sender, getConfig().getString("command-messages.unknown-arg"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                reloadPluginConfig();
                sendMessage(sender, getConfig().getString("command-messages.reload-success"));
            }
            case "restart" -> {
                restartBroadcasts();
                sendMessage(sender, getConfig().getString("command-messages.restart-success"));
            }
            default -> sendMessage(sender, getConfig().getString("command-messages.unknown-arg"));
        }
        return true;
    }

    private void sendMessage(CommandSender sender, String raw) {
        if (raw == null) return;
        String parsed = PlaceholderAPI.setPlaceholders(
                sender instanceof Player ? (Player) sender : null, raw
        );
        sender.sendMessage(mm.deserialize(prefix).append(mm.deserialize(parsed)));
    }
}
