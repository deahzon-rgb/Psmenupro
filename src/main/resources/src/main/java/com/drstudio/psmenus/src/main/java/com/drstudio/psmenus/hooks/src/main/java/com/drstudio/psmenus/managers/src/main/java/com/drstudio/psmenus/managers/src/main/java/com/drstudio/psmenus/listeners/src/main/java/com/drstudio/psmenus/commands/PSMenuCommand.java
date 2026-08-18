package com.drstudio.psmenus.commands;

import com.drstudio.psmenus.PSMenusPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PSMenuCommand implements CommandExecutor {

    private final PSMenusPlugin plugin;

    public PSMenuCommand(PSMenusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("psmenu.admin")) {
                sender.sendMessage(color("&c✘ No tienes permiso para usar este comando."));
                return true;
            }

            plugin.loadConfigurations();
            sender.sendMessage(color("&a✔ Configuración de PSMenusPro (DrStudio) recargada correctamente."));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        plugin.getMenuManager().openMenu(player, "main-menu");
        return true;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
