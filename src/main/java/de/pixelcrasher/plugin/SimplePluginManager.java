package de.pixelcrasher.plugin;

import com.google.common.base.Preconditions;
import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.client.registry.RegistryManager;
import de.pixelcrasher.command.Command;
import de.pixelcrasher.command.CommandMap;
import de.pixelcrasher.command.PluginCommandYamlParser;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.EventPriority;
import de.pixelcrasher.event.HandlerList;
import de.pixelcrasher.event.Listener;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimplePluginManager implements PluginManager {

    private final PixelCrasher client;

    private final Map<Pattern, PluginLoader> fileAssociations = new HashMap<>();
    private final List<Plugin> plugins = new ArrayList<>();
    private final Map<String, Plugin> lookupNames = new HashMap<>();

    private final CommandMap commandMap;
    private final RegistryManager registryManager;

    public SimplePluginManager(PixelCrasher client, CommandMap commandMap, RegistryManager registryManager) {
        this.client = client;
        this.commandMap = commandMap;
        this.registryManager = registryManager;
        this.plugins.add(this.client);
    }

    @Override
    public void registerInterface(Class<? extends PluginLoader> pluginLoader) {
        PluginLoader instance;
        try {
            instance = pluginLoader.getConstructor(new Class[]{PixelCrasher.class}).newInstance(this.client);
        } catch (NoSuchMethodException e) {
            String name = pluginLoader.getName();
            throw new IllegalArgumentException(String.format("Class %s does not have a public %s(PixelCrasher) constructor", name, name), e);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unexpected exception %s while attempting to construct a new instance of %s", e.getClass().getName(), pluginLoader.getName()), e);
        }

        Pattern[] patterns = instance.getPluginFromFilters();
        synchronized (this) {
            for (Pattern pattern : patterns) this.fileAssociations.put(pattern, instance);
        }
    }

    @Override
    public synchronized Plugin getPlugin(String name) {
        return this.lookupNames.get(name);
    }

    @Override
    public Plugin[] getPlugins() {
        return this.plugins.toArray(new Plugin[0]);
    }

    @Override
    public boolean isPluginEnabled(String name) {
        return this.isPluginEnabled(this.getPlugin(name));
    }

    @Override
    public boolean isPluginEnabled(Plugin plugin) {
        if(plugin != null && this.plugins.contains(plugin)) return plugin.isEnabled();
        return false;
    }

    @Override
    public Plugin loadPlugin(File source) throws InvalidPluginException, UnknownDependencyException {
        Preconditions.checkArgument(source != null, "File cannot be null");
        Set<Pattern> filters = this.fileAssociations.keySet();
        Plugin plugin = null;
        for(Pattern filter : filters) {
            String name = source.getName();
            Matcher matcher = filter.matcher(name);
            if(matcher.find()) {
                PluginLoader loader = this.fileAssociations.get(filter);
                plugin = loader.loadPlugin(source);
            }
        }

        if(plugin != null) {
            this.plugins.add(plugin);
            this.lookupNames.put(plugin.getDescription().getName(), plugin);
        }
        return plugin;
    }

    @Override
    public Plugin[] loadPlugins(File sourceDirectory) {
        Preconditions.checkArgument(sourceDirectory != null, "Directory cannot be null");
        Preconditions.checkArgument(sourceDirectory.isDirectory(), "Source must be a directory");
        List<Plugin> result = new ArrayList<>();
        Set<Pattern> filters = this.fileAssociations.keySet();

        Map<String, File> plugins = new HashMap<>();
        Set<String> loadedPlugins = new HashSet<>();
        Map<String, Collection<String>> dependencies = new HashMap<>();
        Map<String, Collection<String>> softDependencies = new HashMap<>();
        for(File file : Objects.requireNonNull(sourceDirectory.listFiles())) {
            PluginLoader loader = null;
            for(Pattern filter : filters) {
                Matcher matcher = filter.matcher(file.getName());
                if(matcher.find()) loader = this.fileAssociations.get(filter);
            }

            if(loader != null) {
                PluginDescription description = null;
                try {
                    description = loader.getPluginDescription(file);

                    File oldPlugin = plugins.put(description.getName(), file);
                    if(oldPlugin != null)
                        this.client.getLogger().error(String.format(
                                "Ambiguous plugin name `%s' for files `%s' and `%s' in `%s'",
                                new Object[] {
                                        description.getName(),
                                        file.getPath(),
                                        oldPlugin.getPath(),
                                        sourceDirectory.getPath()
                                })
                        );

                    Collection<String> softDependencySet = description.getSoftDepend();
                    if(softDependencySet != null && !softDependencySet.isEmpty()) {
                        if(softDependencies.containsKey(description.getName()))
                            softDependencies.get(description.getName()).addAll(softDependencySet);
                        else
                            softDependencies.put(description.getName(), new LinkedList<>(softDependencySet));
                    }

                    Collection<String> dependencySet = description.getDepend();
                    if(dependencySet != null && !dependencySet.isEmpty()) {
                        if(dependencies.containsKey(description.getName()))
                            dependencies.get(description.getName()).addAll(dependencySet);
                        else
                            dependencies.put(description.getName(), new LinkedList<>(dependencySet));
                    }

                    Collection<String> loadBeforeSet = description.getLoadBefore();
                    if(loadBeforeSet != null && !loadBeforeSet.isEmpty()) {
                        for(String target : loadBeforeSet) {
                            if(softDependencies.containsKey(target)) {
                                softDependencies.get(target).add(description.getName());
                            } else {
                                Collection<String> tempDeps = new LinkedList<>();
                                tempDeps.add(description.getName());
                                softDependencies.put(target, tempDeps);
                            }
                        }
                    }

                } catch (InvalidDescriptionException e) {
                    this.client.getLogger().error("Could not load '" + file.getPath() + "' in folder '" + sourceDirectory.getPath() + "'", e);
                }
            }
        }

        while(!plugins.isEmpty()) {
            boolean missingDependency = true;
            Iterator<Map.Entry<String, File>> pluginIterator = plugins.entrySet().iterator();
            while(pluginIterator.hasNext()) {
                Map.Entry<String, File> entry = pluginIterator.next();
                String plugin = entry.getKey();
                if(dependencies.containsKey(plugin)) {
                    Iterator<String> dependencyIterator = dependencies.get(plugin).iterator();
                    while(dependencyIterator.hasNext()) {
                        String dependency = dependencyIterator.next();
                        if(loadedPlugins.contains(dependency)) {
                            dependencyIterator.remove();
                            continue;
                        }
                        if(!plugins.containsKey(dependency)) {
                            missingDependency = false;
                            pluginIterator.remove();
                            softDependencies.remove(plugin);
                            dependencies.remove(plugin);
                            this.client.getLogger().error("Could not load '" + entry.getValue().getPath() + "' in folder '" + sourceDirectory.getPath() + "'", new UnknownDependencyException("Unknown dependency " + dependency + ". Please download and install " + dependency + " to run this plugin."));
                            break;
                        }
                    }
                    if(dependencies.containsKey(plugin) && dependencies.get(plugin).isEmpty()) dependencies.remove(plugin);
                }
                if(softDependencies.containsKey(plugin)) {
                    softDependencies.get(plugin).removeIf(dependency -> !plugins.containsKey(dependency));
                    if(softDependencies.get(plugin).isEmpty()) softDependencies.remove(plugin);
                }
                if(!dependencies.containsKey(plugin) && !softDependencies.containsKey(plugin)) {
                    File file = plugins.get(plugin);
                    pluginIterator.remove();
                    try {
                        Plugin loadedPlugin = loadPlugin(file);
                        if(loadedPlugin != null) {
                            result.add(loadedPlugin);
                            loadedPlugins.add(loadedPlugin.getName());
                            continue;
                        }
                        this.client.getLogger().error("Could not load '" + file.getPath() + "' in folder '" + sourceDirectory.getPath() + "'");
                    } catch (InvalidPluginException e) {
                        this.client.getLogger().error("Could not load '{}' in folder '{}'", file.getPath(), sourceDirectory.getPath(), e);
                    }
                }
            }
            if(missingDependency) {
                pluginIterator = plugins.entrySet().iterator();
                while(pluginIterator.hasNext()) {
                    Map.Entry<String, File> entry = pluginIterator.next();
                    String plugin = entry.getKey();
                    if(!dependencies.containsKey(plugin)) {
                        softDependencies.remove(plugin);
                        missingDependency = false;
                        File file = entry.getValue();
                        pluginIterator.remove();

                        try {
                            Plugin loadedPlugin = loadPlugin(file);
                            if(loadedPlugin != null) {
                                result.add(loadedPlugin);
                                loadedPlugins.add(loadedPlugin.getName());
                                continue;
                            }
                            this.client.getLogger().error("Could not load '" + file.getPath() + "' in folder '" + sourceDirectory.getPath() + "'");
                        } catch (InvalidPluginException e) {
                            this.client.getLogger().error("Could not load '{}' in folder '{}'", file.getPath(), sourceDirectory.getPath(), e);
                        }
                    }
                }
            }
            if(missingDependency) {
                softDependencies.clear();
                dependencies.clear();
                Iterator<File> failedPlugins = plugins.values().iterator();
                while(failedPlugins.hasNext()) {
                    File file = failedPlugins.next();
                    failedPlugins.remove();
                    this.client.getLogger().error("Could not load '" + file.getPath() + "' in folder '" + sourceDirectory.getPath() + "': circular dependency detected");
                }
            }
        }

        return result.toArray(new Plugin[0]);
    }

    @Override
    public void disablePlugins() {
        Plugin[] plugins = getPlugins();
        for(Plugin plugin : plugins) disablePlugin(plugin);
    }

    @Override
    public void clearPlugins() {
        synchronized (this) {
            disablePlugins();
            this.plugins.clear();
            this.lookupNames.clear();
            HandlerList.unregisterAll();
            this.fileAssociations.clear();
        }
    }

    @Override
    public <T extends Event> T call(T event) {
        if(event.isAsynchronous()) {
            if(Thread.holdsLock(this))
                throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from inside synchronized code.");

        }
        return fireEvent(event);
    }

    private <T extends Event> T fireEvent(T event) {
        HandlerList handlers = event.getHandlers();

        RegisteredListener[] listeners = handlers.getRegisteredListeners();
        for(RegisteredListener listener : listeners) {
            if(listener.getPlugin().isEnabled()) {
                try {
                    listener.callEvent(event);
                } catch (Throwable e) {
                    this.client.getLogger().error("Could not pass event {} to {}", event.getEventName(), listener.getPlugin().getDescription().getFullName(), e);
                }
            }
        }
        return event;
    }

    @Override
    public void registerEvents(Listener listener, Plugin provider) {
        if(!provider.isEnabled()) throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
        for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : provider.getPluginLoader().createRegisteredListeners(listener, provider).entrySet())
            getEventListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
    }

    @Override
    public void registerEvent(Class<? extends Event> targetEvent, Listener listener, EventPriority priority, EventExecutor executor, Plugin provider) {
        registerEvent(targetEvent, listener, priority, executor, provider, false);
    }

    @Override
    public void registerEvent(Class<? extends Event> targetEvent, Listener listener, EventPriority priority, EventExecutor executor, Plugin provider, boolean ignoreCancelled) {
        Preconditions.checkArgument(listener != null, "Listener cannot be null");
        Preconditions.checkArgument(priority != null, "Priority cannot be null");
        Preconditions.checkArgument(executor != null, "Executor cannot be null");
        Preconditions.checkArgument(provider != null, "Plugin cannot be null");

        if(!provider.isEnabled()) throw new IllegalPluginAccessException("Plugin attempted to register " + targetEvent + " while not enabled");
        getEventListeners(targetEvent).register(new RegisteredListener(listener, executor, priority, provider, ignoreCancelled));
    }

    private HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            if (!Modifier.isStatic(method.getModifiers()))
                throw new IllegalAccessException("getHandlerList must be static");
            return (HandlerList)method.invoke(type);
        } catch (Exception e) {
            throw new IllegalPluginAccessException("Error while registering listener for event type " + type.toString() + ": " + e.toString());
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null &&
                    !clazz.getSuperclass().equals(Event.class) &&
                    Event.class.isAssignableFrom(clazz.getSuperclass()))
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
        }
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if(!plugin.isEnabled()) {
            List<Command> pluginCommands = PluginCommandYamlParser.parse(plugin);
            if(!pluginCommands.isEmpty()) this.commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
            try {
                plugin.getPluginLoader().enablePlugin(plugin);
            } catch (Throwable e) {
                this.client.getLogger().error("Error occurred (in the plugin loader) while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", e);
            }
            HandlerList.bakeAll();
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if(plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin);
            } catch (Throwable e) {
                this.client.getLogger().error("Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", e);
            }

            try {
                HandlerList.unregisterAll(plugin);
            } catch (Throwable e) {
                this.client.getLogger().error("Error occurred (in the event handler) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)", e);
            }

        }
    }

    @Override
    public Command getCommand(String command) {
        return this.commandMap.getCommand(command);
    }

    public boolean isDepending(PluginDescription first, PluginDescription second) {
        return first.getDepend().contains(second.getName()) || first.getSoftDepend().contains(second.getName());
    }

    @SuppressWarnings("unchecked")
    public <T extends PluginLoader> T fetchLoader(Class<T> clazz) {
        for(PluginLoader loader : this.fileAssociations.values()) {
            if(clazz.isInstance(loader)) return (T) loader;
        }
        return null;
    }

}
