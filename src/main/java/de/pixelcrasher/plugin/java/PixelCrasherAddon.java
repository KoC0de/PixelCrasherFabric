package de.pixelcrasher.plugin.java;

import com.mojang.logging.LogUtils;
import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.util.configuration.FileConfiguration;
import de.pixelcrasher.util.configuration.YamlConfiguration;
import de.pixelcrasher.command.Command;
import de.pixelcrasher.plugin.*;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class PixelCrasherAddon extends PluginBase {

    private boolean enabled;

    private PluginLoader pluginLoader;
    private PixelCrasher client;
    private File file;
    private PluginDescription description;
    private File dataFolder;
    private ClassLoader classLoader;
    private FileConfiguration newConfig;
    private File configFile;
    private Logger logger;

    public PixelCrasherAddon() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if(!(classLoader instanceof PluginClassLoader))
            throw new IllegalStateException("PixelCrasherAddon requires " + PluginClassLoader.class.getName());
        ((PluginClassLoader)classLoader).initialize(this);
    }

    protected PixelCrasherAddon(JavaPluginLoader loader, PluginDescription description, File dataFolder, File file) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if((classLoader instanceof PluginClassLoader))
            throw new IllegalStateException("Cannot use initialization constructor at runtime");
        init(loader, loader.client, description, dataFolder, file, classLoader);
    }

    public void init(PluginLoader loader, PixelCrasher client, PluginDescription description, File dataFolder, File file, ClassLoader classLoader) {
        this.pluginLoader = loader;
        this.client = client;
        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;
        this.classLoader = classLoader;
        this.configFile = new File(dataFolder, "config.yml");
        this.logger = LogUtils.getLogger();
        this.reloadConfig();
    }

    @Override
    public File getDataFolder() {
        return this.dataFolder;
    }

    @Override
    public PluginDescription getDescription() {
        return this.description;
    }

    @Override
    public FileConfiguration getConfig() {
        return this.newConfig;
    }

    @Override
    public InputStream getResource(String resourcePath) {
        try {
            URL url = getClassLoader().getResource(resourcePath);
            if(url == null) return null;
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ignored) {}
        return null;
    }

    protected final ClassLoader getClassLoader() {
        return this.classLoader;
    }

    protected final void setEnabled(boolean enabled) {
        if(this.enabled != enabled) {
            this.enabled = enabled;
            if(this.enabled) this.onEnable();
            else this.onDisable();
        }
    }

    @Override
    public void saveConfig() {
        try {
            getConfig().save(this.configFile);
        } catch (IOException ex) {
            this.logger.error("Could not save config to " + this.configFile, ex);
        }
    }

    @Override
    public void saveDefaultConfig() {
        if(!this.configFile.exists()) this.saveResource("config.yml", false);
    }

    @Override
    public void saveResource(String path, boolean override) {
        path = path.replace('\\', '/');
        InputStream in = getResource(path);
        if(in == null) throw new IllegalArgumentException("The embedded resource '" + path + "' cannot be found in " + this.file);
        File outFile = new File(this.dataFolder, path);
        int lastIndex = path.lastIndexOf('/');
        File outDir = new File(this.dataFolder, path.substring(0, Math.max(lastIndex, 0)));
        if(!outDir.exists()) outDir.mkdirs();
        try {
            if (!outFile.exists() || override) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while((len = in.read(buf)) > 0)
                    out.write(buf, 0, len);
                out.close();
                in.close();
            } else {
                this.logger.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException e) {
            this.logger.error("Could not save " + outFile.getName() + " to " + outFile, e);
        }
    }

    @Override
    public void reloadConfig() {
        this.newConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defConfigStream = getResource("config.yml");
        if(defConfigStream == null) return;
        this.newConfig.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.pluginLoader;
    }

    public PixelCrasher getClient() {
        return this.client;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @Override
    public void onLoad() {}

    public Logger getLogger() {
        return this.logger;
    }

    public Command getCommand(String name) {
        return this.client.getCommand(name);
    }
}
