package com.drstudio.psmenus.managers;

import com.drstudio.psmenus.PSMenusPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {

    private final PSMenusPlugin plugin;
    private final Map<UUID, BukkitTask> activeTeleports = new ConcurrentHashMap<>();

    public TeleportManager(PSMenusPlugin plugin) {
        this.plugin = plugin;
    }

    public void startCountdown(Player player, Location targetLocation) {
        cancelTeleport(player);

        int delay = Math.max(1, plugin.getConfig().getInt("config.teleport.countdown-seconds", 3));
        Location startLoc = player.getLocation().clone();
        player.closeInventory();

        final int[] secondsLeft = {delay};

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                cancelTeleport(player);
                return;
            }

            if (player.getLocation().distanceSquared(startLoc) > 0.1) {
                player.sendMessage(color("&c✘ Teletransporte cancelado por movimiento."));
                cancelTeleport(player);
                return;
            }

            if (secondsLeft[0] <= 0) {
                player.teleport(targetLocation != null ? targetLocation : startLoc);
                player.sendMessage(color("&a✔ Teletransporte completado."));
                cancelTeleport(player);
                return;
            }

            player.sendMessage(color("&e» Teletransportándote en &a" + secondsLeft[0] + " &esegundos..."));
            secondsLeft[0]--;
        }, 0L, 20L);

        activeTeleports.put(player.getUniqueId(), task);
    }

    public void cancelTeleport(Player player) {
        BukkitTask task = activeTeleports.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }

    public void cancelAll() {
        activeTeleports.values().forEach(BukkitTask::cancel);
        activeTeleports.clear();
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
