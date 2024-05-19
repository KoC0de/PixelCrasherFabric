package de.pixelcrasher.plugin;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.Listener;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public interface PluginLoader {

    Plugin loadPlugin(File source) throws InvalidPluginException, UnknownDependencyException;
    PluginDescription getPluginDescription(File source) throws InvalidDescriptionException;
    Pattern[] getPluginFromFilters();

    Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin provider);

    void enablePlugin(Plugin plugin);
    void disablePlugin(Plugin plugin);

}
