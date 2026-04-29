package org.nevetime.autoMessage;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BroadcastManager {
    private final List<BukkitTask> tasks = new ArrayList<>();
    private final AutoMessageUtils utils = new AutoMessageUtils();
    private final AutoMessage plugin;
    private final MiniMessage mm;

    public BroadcastManager(AutoMessage plugin) {
        this.plugin = plugin;
        this.mm = plugin.getMiniMessage();
    }

    public void restart() {
        cancelTasks();
        loadBroadcasts();
    }

    public void pause() {
        cancelTasks();
    }

    public void resume() {
        loadBroadcasts();
    }

    public void stop() {
        cancelTasks();
    }

    private void cancelTasks() {
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
    }

    private void loadBroadcasts() {
        List<Map<?, ?>> list = plugin.getConfig().getMapList("messages");
        if (list.isEmpty()) {
            plugin.getLogger().severe("Не удалось найти или загрузить секцию messages!");
        } else {
            plugin.getLogger().info("Загружено " + list.size() + " сообщений из конфига.");
        }

        for (Map<?, ?> map : list) {
            String type = (String) map.get("type");

            int period;
            if (map.get("period") instanceof Number n) {
                int value = n.intValue();
                period = value > 1000 ? value : value * 20;
            } else {
                period = 6000;
            }

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        switch (type) {
                            case "actionbar" -> {
                                if (map.get("text") == null) {
                                    String raw = map.get("text").toString();

                                    String stripped = (raw);
                                    String parsed = PlaceholderAPI.setPlaceholders(player, stripped);
                                    Component comp = mm.deserialize(parsed);
                                    player.sendActionBar(comp);
                                }
                            }
                            case "title" -> {
                                if (map.get("title") != null && map.get("subtitle") != null) {
                                    String rawTitle = map.get("title").toString();
                                    String rawSub = map.get("subtitle").toString();

                                    String strippedTitle = utils.stripInteractiveTags(rawTitle);
                                    String strippedSub = utils.stripInteractiveTags(rawSub);

                                    String parsedTitle = PlaceholderAPI.setPlaceholders(player, strippedTitle);
                                    String parsedSub = PlaceholderAPI.setPlaceholders(player, strippedSub);

                                    Component compTitle = mm.deserialize(parsedTitle);
                                    Component compSub = mm.deserialize(parsedSub);

                                    player.showTitle(Title.title(compTitle, compSub));
                                }
                            }
                            default -> {
                                if (map.get("text") != null) {
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
                }
            }.runTaskTimer(this.plugin, period, period);

            tasks.add(task);
        }
    }
}
