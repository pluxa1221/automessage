package org.nevetime.autoMessage.tasks;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.nevetime.autoMessage.AutoMessageUtils;

public class ActionBarMessageTask extends BukkitRunnable {

    private final AutoMessageUtils utils = new AutoMessageUtils();
    private final MiniMessage mm = MiniMessage.miniMessage();

    private final String raw;

    public ActionBarMessageTask(String raw) {
        this.raw = raw;
    }

    @Override
    public void run() {
        if (this.raw != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String stripped = utils.stripInteractiveTags(raw);
                String parsed = PlaceholderAPI.setPlaceholders(player, stripped);
                Component comp = mm.deserialize(parsed);
                player.sendActionBar(comp);
            }
        }
    }
}
