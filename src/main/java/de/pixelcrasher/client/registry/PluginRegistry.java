package de.pixelcrasher.client.registry;

import de.pixelcrasher.event.EventHandler;
import de.pixelcrasher.event.Listener;
import de.pixelcrasher.event.core.RegistryInitEvent;
import de.pixelcrasher.plugin.Plugin;
import de.pixelcrasher.plugin.PluginManager;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Supplier;

public class PluginRegistry<T> implements Listener {

    private final Plugin provider;
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final Collection<RegistryObject<? extends T>> objects;
    private final List<Runnable> postRegister = new ArrayList<>();

    private boolean frozen = false;

    public static <B> PluginRegistry<B> create(Plugin provider, ResourceKey<? extends Registry<B>> registryKey) {
        return new PluginRegistry<>(provider, registryKey);
    }

    private PluginRegistry(Plugin provider, ResourceKey<? extends Registry<T>> registryKey) {
        this.provider = provider;
        this.registryKey = registryKey;
        this.objects = new HashSet<>();
    }

    @EventHandler
    private void onRegister(RegistryInitEvent e) {
        if(this.frozen) return;

        synchronized (this.objects) {
            for (RegistryObject<? extends T> object : objects) {
                if(e.getRoot() instanceof MappedRegistry<? extends Registry<?>> root) {
                    //if(root.frozen()) throw new IllegalStateException("Registry is already frozen");

                   /* root.getOptional(this.registryKey.location())
                            .filter(registry -> registry instanceof MappedRegistry)
                            .map(registry -> (MappedRegistry<T>) ((MappedRegistry) registry).unfreeze())
                            .filter(registry -> !registry.frozen())
                            .ifPresentOrElse(registry -> {
                                Registry.register(registry, object.getLocation(), object.getSupplier().get());
                                object.onRegister();
                                registry.freeze();
                            }, () -> {
                                this.provider.getLogger().error("Could not register {} to {} registry", object.getLocation(), registryKey);
                            });
                    */
                }
            }

            synchronized (this.postRegister) {
                this.postRegister.forEach(Runnable::run);
            }
        }
        this.frozen = true;

    }

    public void post(Runnable runnable) {
        if(this.frozen) throw new IllegalStateException("This plugin registry is already frozen");
        this.postRegister.add(runnable);
    }

    public void registerToContext(PluginManager context) {
        context.registerEvents(this, this.provider);
    }

    public <E extends T> RegistryObject<E> register(String name, Supplier<E> supplier) {
        if(this.frozen) throw new IllegalStateException("This plugin registry is already frozen");
        RegistryObject<E> obj = new RegistryObject<E>(new ResourceLocation(this.provider.getDescription().getName().toLowerCase(Locale.ENGLISH).trim(), name), supplier, this.registryKey);
        this.objects.add(obj);
        return obj;
    }

    public Plugin getProvider() {
        return this.provider;
    }

}
