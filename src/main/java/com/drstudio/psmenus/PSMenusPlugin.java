package com.drstudio.psmenus;

import com.drstudio.psmenus.commands.PSMenuCommand;
import com.drstudio.psmenus.hooks.ProtectionStonesHook;
import com.drstudio.psmenus.listeners.MenuListener;
import com.drstudio.psmenus.managers.MenuManager;
import com.drstudio.psmenus.managers.TeleportManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PSMenusPlugin extends JavaPlugin {

    private ProtectionStonesHook protectionStonesHook;
    private MenuManager menuManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.protectionStonesHook = new ProtectionStonesHook(this);
        this.teleportManager = new TeleportManager(this);
        this.menuManager = new MenuManager(this);

        if (getCommand("psmenu") != null) {
            getCommand("psmenu").setExecutor(new PSMenuCommand(this));
        }

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);

        getLogger().info("PSMenusPro ha sido activado correctamente.");
    }

    @Override
    public void onDisable() {
        getLogger().info("PSMenusPro ha sido desactivado.");
    }

    public ProtectionStonesHook getProtectionStonesHook() {
        return protectionStonesHook;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
}
