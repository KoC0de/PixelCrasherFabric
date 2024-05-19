package de.pixelcrasher.plugin.internal;

import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.plugin.*;
import de.pixelcrasher.plugin.java.JavaPluginLoader;
import de.pixelcrasher.plugin.java.PixelCrasherAddon;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class CorePluginLoader {

    private final Logger logger;
    private PixelCrasher client;

    public CorePluginLoader(Logger logger) {
        this.logger = logger;
    }

    public PixelCrasher loadCore() {
        try {
            this.client = this.loadPlugin(PixelCrasher.class);
        } catch (InvalidPluginException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            this.logger.error("Could not load core plugin.", e);
        }
        return this.client;
    }

    public <T extends Plugin> T loadPlugin(Class<T> plugin) throws InvalidPluginException, UnknownDependencyException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T corePlugin;
        try {
            corePlugin = plugin.getConstructor(CorePluginLoader.class, Logger.class, PluginDescription.class).newInstance(this, this.logger, this.getPluginDescription(ClassLoader.getSystemResourceAsStream("pixelcrasher.yml")));
            corePlugin.onLoad();
        } catch (InvalidDescriptionException e) {
            throw new InvalidPluginException(e);
        }

        return corePlugin;
    }

    public PluginDescription getPluginDescription(InputStream description) throws InvalidDescriptionException {
        return new PluginDescription(description);
    }


}
