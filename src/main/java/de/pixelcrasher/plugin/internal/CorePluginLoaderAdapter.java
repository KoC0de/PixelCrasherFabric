package de.pixelcrasher.plugin.internal;

import com.google.common.base.Preconditions;
import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.EventException;
import de.pixelcrasher.event.EventHandler;
import de.pixelcrasher.event.Listener;
import de.pixelcrasher.plugin.*;
import de.pixelcrasher.plugin.java.JavaPluginLoader;
import de.pixelcrasher.plugin.java.PixelCrasherAddon;
import de.pixelcrasher.plugin.java.PluginClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class CorePluginLoaderAdapter implements PluginLoader {

    private final CorePluginLoader master;

    public CorePluginLoaderAdapter(CorePluginLoader master) {
        this.master = master;
    }

    @Override
    public Plugin loadPlugin(File source) throws InvalidPluginException, UnknownDependencyException {
        throw new InvalidPluginException("Cannot use core loader to load a plugin from file! Use CoreLoaderPluginAdapter#getMaster#loadPlugin(Class) instead.");
    }

    @Override
    public PluginDescription getPluginDescription(File source) throws InvalidDescriptionException {
        try {
            return this.master.getPluginDescription(new FileInputStream(source));
        } catch (FileNotFoundException e) {
            throw new IllegalAccessError("Cannot use core loader to load an external description from file! Use JavaPluginLoader#getPluginDescription(File) instead.");
        }
    }

    @Override
    public Pattern[] getPluginFromFilters() {
        return new Pattern[0];
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin provider) {
        return ((SimplePluginManager)PixelCrasher.getInstance().getPluginManager()).fetchLoader(JavaPluginLoader.class).createRegisteredListeners(listener, provider);
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        Preconditions.checkArgument(plugin.getPluginLoader() instanceof CorePluginLoaderAdapter, "Plugin is not associated with this PluginLoader");
        if(!plugin.isEnabled()) {
            plugin.onEnable();
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        Preconditions.checkArgument(plugin.getPluginLoader() instanceof CorePluginLoaderAdapter, "Plugin is not associated with this PluginLoader");
    }
}
