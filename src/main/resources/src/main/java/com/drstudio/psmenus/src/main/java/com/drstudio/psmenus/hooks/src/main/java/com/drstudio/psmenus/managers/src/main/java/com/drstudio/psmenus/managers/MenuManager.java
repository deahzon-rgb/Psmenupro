package com.drstudio.psmenus.managers;

import com.drstudio.psmenus.PSMenusPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MenuManager {

    private final PSMenusPlugin plugin;
    private final Map<UUID, String> openSessions = new ConcurrentHashMap<>();

    public MenuManager(PSMenusPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player, String menuId) {
        ConfigurationSection section = plugin.getMenusConfig().getConfigurationSection("inventories." + menuId);
        if (section == null) {
            player.sendMessage(color("&cEl menú '" + menuId + "' no existe."));
            return;
        }

        int rawSize = section.getInt("size", 27);
        int size = Math.min(54, Math.max(9, (rawSize / 9) * 9));
        String title = color(section.getString("name", "&8DrStudio - Protecciones"));

        Inventory inv = Bukkit.createInventory(null, size, title);

        renderSection(player, inv, section.getConfigurationSection("items"));
        renderSection(player, inv, section.getConfigurationSection("custom-items"));

        player.openInventory(inv);
        openSessions.put(player.getUniqueId(), menuId);
    }

    private void renderSection(Player player, Inventory inv, ConfigurationSection section) {
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSec = section.getConfigurationSection(key);
            if (itemSec == null || !itemSec.getBoolean("enabled", true)) continue;

            int slot = itemSec.getInt("slot", -1);
            if (slot < 0 || slot >= inv.getSize()) continue;

            Material mat = parseMaterial(itemSec.getString("material", "STONE"));
            ItemStack item = new ItemStack(mat, Math.max(1, itemSec.getInt("amount", 1)));
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(color(replacePlaceholders(itemSec.getString("display_name", ""), player)));

                List<String> lore = new ArrayList<>();
                for (String line : itemSec.getStringList("lore")) {
                    lore.add(color(replacePlaceholders(line, player)));
                }
                meta.setLore(lore);

                if (itemSec.contains("model_data")) {
                    meta.setCustomModelData(itemSec.getInt("model_data"));
                }

                for (String flagStr : itemSec.getStringList("item_flags")) {
                    try {
                        meta.addItemFlags(ItemFlag.valueOf(flagStr.toUpperCase()));
                    } catch (Exception ignored) {}
                }

                item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
        }
    }

    public void executeActions(Player player, List<String> actions) {
        for (String action : actions) {
            String trimmed = action.trim();
            if (trimmed.startsWith("[openmenu]")) {
                openMenu(player, trimmed.substring(10).trim());
            } else if (trimmed.startsWith("[message]")) {
                player.sendMessage(color(replacePlaceholders(trimmed.substring(9).trim(), player)));
            } else if (trimmed.startsWith("[close]")) {
                player.closeInventory();
            } else if (trimmed.startsWith("[command]")) {
                Bukkit.dispatchCommand(player, replacePlaceholders(trimmed.substring(9).trim(), player));
            }
        }
    }

    public String getOpenMenu(UUID uuid) { return openSessions.get(uuid); }
    public void removeSession(UUID uuid) { openSessions.remove(uuid); }
    public void clearSessions() { openSessions.clear(); }

    private Material parseMaterial(String matName) {
        try {
            return Material.valueOf(matName.toUpperCase());
        } catch (Exception e) {
            return Material.BARRIER;
        }
    }

    private String replacePlaceholders(String text, Player player) {
        return text.replace("%player_name%", player.getName());
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }
}
