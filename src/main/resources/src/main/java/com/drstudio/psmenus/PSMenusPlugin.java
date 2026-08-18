package com.drstudio.psmenus;

import com.drstudio.psmenus.commands.PSMenuCommand;
import com.drstudio.psmenus.hooks.ProtectionStonesHook;
import com.drstudio.psmenus.listeners.MenuListener;
import com.drstudio.psmenus.managers.MenuManager;
import com.drstudio.psmenus.managers.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PSMenusPlugin extends JavaPlugin {

    private static PSMenusPlugin instance;
    private MenuManager menuManager;
    private TeleportManager teleportManager;
    private ProtectionStonesHook psHook;

    private FileConfiguration menusConfig;
    private FileConfiguration messagesConfig;
    private FileConfiguration flagsConfig;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResourceIfNotExists("menus.yml");
        saveResourceIfNotExists("messages.yml");
        saveResourceIfNotExists("flags.yml");

        loadConfigurations();

        if (Bukkit.getPluginManager().isPluginEnabled("ProtectionStones")) {
            this.psHook = new ProtectionStonesHook();
        } else {
            getLogger().severe("✘ ProtectionStones no fue encontrado. Deshabilitando plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.teleportManager = new TeleportManager(this);
        this.menuManager = new MenuManager(this);

        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
        if (getCommand("psmenu") != null) {
            getCommand("psmenu").setExecutor(new PSMenuCommand(this));
        }

        getLogger().info("==========================================");
        getLogger().info("  PSMenusPro v1.0.0 - DrStudio");
        getLogger().info("  ✔ Plugin activado e integrado con éxito.");
        getLogger().info("==========================================");
    }

    @Override
    public void onDisable() {
        if (teleportManager != null) teleportManager.cancelAll();
        if (menuManager != null) menuManager.clearSessions();
    }

    public void loadConfigurations() {
        reloadConfig();
        menusConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "menus.yml"));
        messagesConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
        flagsConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "flags.yml"));
    }

    private void saveResourceIfNotExists(String fileName) {
        File file = new File(getDataFolder(), fileName);
        if (!file.exists()) {
            saveResource(fileName, false);
        }
    }

    public static PSMenusPlugin getInstance() { return instance; }
    public MenuManager getMenuManager() { return menuManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public ProtectionStonesHook getPsHook() { return psHook; }
    public FileConfiguration getMenusConfig() { return menusConfig; }
    public FileConfiguration getMessagesConfig() { return messagesConfig; }
    public FileConfiguration getFlagsConfig() { return flagsConfig; }
}
