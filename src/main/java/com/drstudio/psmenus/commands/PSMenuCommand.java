package com.drstudio.psmenus.commands;

import com.drstudio.psmenus.PSMenusPlugin;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c¡Este comando solo puede ser usado por jugadores!");
            return true;
        }

        Player player = (Player) sender;
        
        // Aquí llamas a tu manager para abrir el menú, por ejemplo:
        // plugin.getMenuManager().openMenu(player);
        
        // O prueba temporalmente con esto para verificar que ya abre algo:
        player.sendMessage("§a¡Abriendo menú de PSMenusPro!");

        return true;
    }
}

