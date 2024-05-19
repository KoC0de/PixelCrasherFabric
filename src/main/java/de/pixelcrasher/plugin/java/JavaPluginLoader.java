package de.pixelcrasher.plugin.java;

import com.google.common.base.Preconditions;
import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.EventException;
import de.pixelcrasher.event.EventHandler;
import de.pixelcrasher.event.Listener;
import de.pixelcrasher.plugin.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class JavaPluginLoader implements PluginLoader {

    protected PixelCrasher client;

    private final Pattern[] fileFilters = new Pattern[] { Pattern.compile("\\.jar$") };
    private final List<PluginClassLoader> loaders = new CopyOnWriteArrayList<>();

    public JavaPluginLoader(PixelCrasher client) {
        this.client = client;
    }

    @Override
    public Plugin loadPlugin(File source) throws InvalidPluginException {
        PluginDescription description;
        PluginClassLoader loader;
        Preconditions.checkArgument(source != null, "File cannot be null");

        if(!source.exists()) throw new InvalidPluginException(new FileNotFoundException(source.getPath() + " does not exists"));

        try {
            description = this.getPluginDescription(source);
        } catch (InvalidDescriptionException e) {
            throw new InvalidPluginException(e);
        }

        File parent = source.getParentFile();
        File dataFolder = new File(parent, description.getName());
        if(dataFolder.exists() && !dataFolder.isDirectory())
            throw new InvalidPluginException(String.format(
                    "Projected datafolder: `%s' for %s (%s) exists and is not a directory",
                    dataFolder,
                    description.getFullName(),
                    source)
            );

        for(String dependency : description.getDepend()) {
            Plugin current = this.client.getPluginManager().getPlugin(dependency);
            if(current == null) throw new UnknownDependencyException("Unknown dependency " + dependency + ". Please download and install " + dependency + " to run this plugin.");
        }
        try {
            loader = new PluginClassLoader(this, getClass().getClassLoader(), description, dataFolder, source);
        } catch (InvalidPluginException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvalidPluginException(e);
        }
        this.loaders.add(loader);
        return loader.plugin;
    }

    @Override
    public PluginDescription getPluginDescription(File source) throws InvalidDescriptionException {
        Preconditions.checkArgument(source != null, "File cannot be null");
        JarFile jar = null;
        InputStream inputStream = null;
        try {
            jar = new JarFile(source);
            JarEntry entry = jar.getJarEntry("plugin.yml");
            if(entry == null) entry = jar.getJarEntry("addon.yml");
            if(entry == null) throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain plugin.yml"));
            inputStream = jar.getInputStream(entry);
            return new PluginDescription(inputStream);
        } catch (IOException | ClassCastException e) {
            throw new InvalidDescriptionException(e);
        } finally {
            if(jar != null)
                try {
                    jar.close();
                } catch (IOException ignored) {}

            if(inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException ignored) {}
        }
    }

    @Override
    public Pattern[] getPluginFromFilters() {
        return this.fileFilters.clone();
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin provider) {
        Set<Method> methods;

        Preconditions.checkArgument(provider != null, "Plugin can not be null");
        Preconditions.checkArgument(listener != null, "Listener can not be null");
        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            Method[] privateMethods = listener.getClass().getDeclaredMethods();

            methods = new HashSet<>(publicMethods.length + privateMethods.length, 1);
            methods.addAll(Arrays.asList(publicMethods));
            methods.addAll(Arrays.asList(privateMethods));
        } catch (NoClassDefFoundError e) {
            provider.getLogger().error("Plugin {} has failed to register events for {} because {} does not exist.", provider.getDescription().getFullName(), listener.getClass(), e.getMessage());
            return ret;
        }
        for(Method method : methods) {
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if(eventHandler == null) continue;
            if(method.isBridge() || method.isSynthetic()) continue;

            Class<?> checkClass;
            if ((method.getParameterTypes()).length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                provider.getLogger().error(provider.getDescription().getFullName() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }

            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<RegisteredListener> listeners = ret.computeIfAbsent(eventClass, k -> new HashSet<>());

            EventExecutor executor = (listener1, event) -> {
                try {
                    if (!eventClass.isAssignableFrom(event.getClass()))
                        return;
                    method.invoke(listener1, event);
                } catch (InvocationTargetException e) {
                    throw new EventException(e.getCause());
                } catch (Throwable e) {
                    throw new EventException(e);
                }
            };

            listeners.add(new RegisteredListener(listener, executor, eventHandler.priority(), provider, eventHandler.ignoreCancelled()));
        }
        return ret;
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        Preconditions.checkArgument(plugin instanceof PixelCrasherAddon, "Plugin is not associated with this PluginLoader");
        if(!plugin.isEnabled()) {
            PixelCrasherAddon addon = (PixelCrasherAddon) plugin;
            addon.getLogger().info("Enabling " + plugin.getDescription().getFullName());

            PluginClassLoader pluginLoader = (PluginClassLoader) addon.getClassLoader();

            if(!this.loaders.contains(pluginLoader)) {
                this.loaders.add(pluginLoader);
                this.client.getLogger().warn("Enabled plugin with unregistered PluginClassLoader " + plugin.getDescription().getFullName());
            }

            try {
                addon.setEnabled(true);
            } catch (Throwable e) {
                this.client.getLogger().error("Error occurred while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", e);
            }

        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        Preconditions.checkArgument(plugin instanceof PixelCrasherAddon, "Plugin is not associated with this PluginLoader");
        if(plugin.isEnabled()) {
            plugin.getLogger().info("Disabling " + plugin.getDescription().getFullName());
            PixelCrasherAddon addon = (PixelCrasherAddon) plugin;
            ClassLoader cloader = addon.getClassLoader();

            try {
                addon.setEnabled(false);
            } catch (Throwable e) {
                this.client.getLogger().error("Error occurred while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", e);
            }

            if(cloader instanceof PluginClassLoader loader) {
                this.loaders.remove(loader);

                try {
                    loader.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public Pattern[] getPluginFileFilters() {
        return this.fileFilters.clone();
    }

    public Class<?> getClassByName(String name, boolean resolve, PluginDescription description) throws ClassNotFoundException {
        for(PluginClassLoader loader : this.loaders) {
            return loader.loadClass0(name, resolve, false, ((SimplePluginManager)this.client.getPluginManager()).isDepending(description, loader.plugin.getDescription()));
        }
        return null;
    }

}
