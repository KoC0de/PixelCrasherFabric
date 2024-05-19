package de.pixelcrasher;

import de.pixelcrasher.client.registry.RegistryManager;
import de.pixelcrasher.util.configuration.FileConfiguration;
import de.pixelcrasher.util.configuration.YamlConfiguration;
import de.pixelcrasher.util.player.ClientPlayer;
import de.pixelcrasher.util.player.IClientPlayer;
import de.pixelcrasher.command.Command;
import de.pixelcrasher.command.CommandMap;
import de.pixelcrasher.command.SimpleCommandMap;
import de.pixelcrasher.command.defaults.HelpCommand;
import de.pixelcrasher.command.defaults.PluginCommand;
import de.pixelcrasher.command.defaults.ReloadCommand;
import de.pixelcrasher.listeners.DebugListener;
import de.pixelcrasher.plugin.*;
import de.pixelcrasher.plugin.internal.CorePluginLoader;
import de.pixelcrasher.plugin.internal.CorePluginLoaderAdapter;
import de.pixelcrasher.plugin.java.JavaPluginLoader;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.Locale;

public class PixelCrasher implements Plugin {

    private static PixelCrasher instance;

    private final Logger logger;
    private final CorePluginLoaderAdapter pluginLoader;
    private final PluginDescription description;

    private PluginManager pluginManager;

    private CommandMap commandMap;
    private RegistryManager registryManager;

    private File dataFolder;

    private File configFile;
    private FileConfiguration configuration;

    private IClientPlayer clientPlayer;

    private boolean enabled;

    public PixelCrasher(CorePluginLoader loader, Logger logger, PluginDescription description) {
        if(instance != null) throw new IllegalStateException("Cannot have two instances of " + this.getClass().getSimpleName());
        instance = this;
        this.pluginLoader = new CorePluginLoaderAdapter(loader);
        this.logger = logger;
        this.description = description;
    }

    @Override
    public void onLoad() {
        this.getLogger().info("Loading PixelCrasher Core Plugin...");
        this.dataFolder = new File("./PixelCrasher/");
        this.configFile = new File(this.getDataFolder(), "configuration.yml");

        File addonDirectory = new File(this.getDataFolder(), "addons");
        if(!addonDirectory.exists()) addonDirectory.mkdirs();

        this.commandMap = new SimpleCommandMap(this);
        this.registryManager = new RegistryManager(this);

        this.pluginManager = new SimplePluginManager(this, this.commandMap, this.registryManager);
        this.pluginManager.registerInterface(JavaPluginLoader.class);

        this.saveDefaultConfig();
        this.reloadConfig();

        Plugin[] plugins = this.pluginManager.loadPlugins(addonDirectory);
        Arrays.stream(plugins).forEach(Plugin::onLoad);

        this.getLogger().info("Loaded everything necessary to enable plugins.");
    }

    @Override
    public void onEnable() {

        this.getLogger().info("Enabling PixelCrasher Core Plugin...");
        this.enabled = true;

        this.registerCommands();
        this.registerListeners();

        this.clientPlayer = new ClientPlayer();

        Arrays.stream(this.pluginManager.getPlugins()).forEach(plugin -> this.pluginManager.enablePlugin(plugin));
        this.registryManager.register();

        this.getLogger().info("PixelCrasher loaded and enabled with all implemented features.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabling PixelCrasher Core Plugin...");
        this.pluginManager.disablePlugins();
        this.enabled = false;
        this.getLogger().info("PixelCrasher unloaded and disabled all plugins.");
    }

    public void reload() {
        this.pluginManager.clearPlugins();
        this.enabled = false;
        this.onLoad();
        System.gc();
        Minecraft.getInstance().reloadResourcePacks();
        this.pluginManager.enablePlugin(this);
    }

    private void registerCommands() {
        this.getCommand("help").setExecutor(new HelpCommand());
        this.getCommand("plugins").setExecutor(new PluginCommand());
        this.getCommand("reload").setExecutor(new ReloadCommand());
    }

    private void registerListeners() {
        if(this.getConfig().getBoolean("developer.debug.debugListener")) {
            this.getPluginManager().registerEvents(new DebugListener(), this);
        }
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
        return this.configuration;
    }

    @Override
    public InputStream getResource(String resourcePath) {
        return ClassLoader.getSystemResourceAsStream(resourcePath);
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
        if(!this.configFile.exists()) this.saveResource("configuration.yml", false);
    }

    @Override
    public void saveResource(String path, boolean override) {
        path = path.replace('\\', '/');
        InputStream in = getResource(path);
        if(in == null) throw new IllegalArgumentException("The resource " + path + " is not implemented as default.");
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
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defConfigStream = getResource("configuration.yml");
        if(defConfigStream == null) return;
        this.configuration.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
    }

    @Override
    public CorePluginLoaderAdapter getPluginLoader() {
        return this.pluginLoader;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public Command getCommand(String name) {
        String alias = name.toLowerCase(Locale.ENGLISH);
        Command command = this.getPluginManager().getCommand(name);
        if (command == null || command.getProvider() != this)
            command = this.getPluginManager().getCommand(this.description.getName().toLowerCase(Locale.ENGLISH) + ":" + alias);
        if (command != null && command.getProvider() == this)
            return command;
        return null;
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }

    @Override
    public String getName() {
        return "Core";
    }

    public IClientPlayer getClientPlayer() {
        return clientPlayer;
    }

    public static PixelCrasher getInstance() {
        return instance;
    }
}
