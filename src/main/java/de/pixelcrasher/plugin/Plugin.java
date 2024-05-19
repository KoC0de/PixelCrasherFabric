package de.pixelcrasher.plugin;

import de.pixelcrasher.util.configuration.FileConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;

public interface Plugin {

    File getDataFolder();

    PluginDescription getDescription();

    FileConfiguration getConfig();

    InputStream getResource(String resourcePath);

    void saveConfig();
    void saveDefaultConfig();
    void saveResource(String path, boolean override);
    void reloadConfig();

    PluginLoader getPluginLoader();

    boolean isEnabled();

    void onDisable();
    void onEnable();
    void onLoad();

    String getName();

    Logger getLogger();

}
