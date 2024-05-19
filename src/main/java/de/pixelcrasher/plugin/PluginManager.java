package de.pixelcrasher.plugin;

import de.pixelcrasher.command.Command;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.EventPriority;
import de.pixelcrasher.event.Listener;

import java.io.File;

public interface PluginManager {

    void registerInterface(Class<? extends PluginLoader> pluginLoader);

    Plugin getPlugin(String name);
    Plugin[] getPlugins();

    boolean isPluginEnabled(String name);
    boolean isPluginEnabled(Plugin plugin);

    Plugin loadPlugin(File source) throws InvalidPluginException, UnknownDependencyException;
    Plugin[] loadPlugins(File source);

    void disablePlugins();
    void clearPlugins();
    <T extends Event> T call(T event);
    void registerEvents(Listener listener, Plugin provider);
    void registerEvent(Class<? extends Event> targetEvent, Listener listener, EventPriority priority, EventExecutor executor, Plugin provider);
    void registerEvent(Class<? extends Event> targetEvent, Listener listener, EventPriority priority, EventExecutor executor, Plugin provider, boolean paramBool);

    void enablePlugin(Plugin plugin);
    void disablePlugin(Plugin plugin);

    Command getCommand(String command);


}
