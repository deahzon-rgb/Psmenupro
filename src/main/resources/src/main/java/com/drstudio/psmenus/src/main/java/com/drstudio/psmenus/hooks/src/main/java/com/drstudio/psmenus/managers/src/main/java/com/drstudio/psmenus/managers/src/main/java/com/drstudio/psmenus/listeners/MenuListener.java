package com.drstudio.psmenus.listeners;

import com.drstudio.psmenus.PSMenusPlugin;
import dev.espi.protectionstones.PSRegion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.List;

public class MenuListener implements Listener {

    private final PSMenusPlugin plugin;

    public MenuListener(PSMenusPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String currentMenu = plugin.getMenuManager().getOpenMenu(player.getUniqueId());
        if (currentMenu == null) return;

        event.setCancelled(true);

        if (event.getClickedInventory() == null || 
            event.getClickedInventory() != event.getView().getTopInventory()) return;

        int slot = event.getRawSlot();
        ConfigurationSection root = plugin.getMenusConfig().getConfigurationSection("inventories." + currentMenu);
        if (root == null) return;

        ConfigurationSection custom = root.getConfigurationSection("custom-items");
        if (custom != null) {
            for (String key : custom.getKeys(false)) {
                ConfigurationSection item = custom.getConfigurationSection(key);
                if (item != null && item.getInt("slot", -1) == slot) {
                    List<String> actions = item.getStringList("actions");
                    plugin.getMenuManager().executeActions(player, actions);
                    return;
                }
            }
        }

        ConfigurationSection items = root.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                ConfigurationSection item = items.getConfigurationSection(key);
                if (item != null && item.getInt("slot", -1) == slot) {
                    handleBuiltIn(player, key);
                    return;
                }
            }
        }
    }

    private void handleBuiltIn(Player player, String actionKey) {
        PSRegion region = plugin.getPsHook().getRegionAt(player.getLocation());

        switch (actionKey.toLowerCase()) {
            case "close" -> player.closeInventory();
            case "back" -> plugin.getMenuManager().openMenu(player, "main-menu");
            case "list" -> plugin.getMenuManager().openMenu(player, "homes");
            case "teleport" -> {
                if (region != null) {
                    plugin.getTeleportManager().startCountdown(player, region.getHome());
                } else {
                    player.sendMessage("§c✘ No estás parado sobre una protección.");
                }
            }
            case "toggle_pvp" -> {
                if (region != null && region.isOwner(player.getUniqueId())) {
                    String currentPvp = plugin.getPsHook().getFlag(region, "pvp");
                    String newPvp = "allow".equalsIgnoreCase(currentPvp) ? "deny" : "allow";
                    plugin.getPsHook().setFlag(region, "pvp", newPvp);
                    player.sendMessage("§a✔ Estado de PvP actualizado a: §e" + newPvp);
                    player.closeInventory();
                } else {
                    player.sendMessage("§c✘ Debes ser el propietario de esta protección.");
                }
            }
            default -> {}
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            plugin.getMenuManager().removeSession(player.getUniqueId());
        }
    }
}
