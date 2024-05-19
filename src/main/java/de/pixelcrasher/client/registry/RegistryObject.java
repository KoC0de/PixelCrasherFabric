package de.pixelcrasher.client.registry;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RegistryObject<T> {

    private final ResourceLocation location;
    private final Supplier<T> supplier;
    private final ResourceKey<?> registryKey;

    private Consumer<RegistryObject<T>> onRegister;

    protected RegistryObject(ResourceLocation location, Supplier<T> supplier, ResourceKey<?> registryKey) {
        this.location = location;
        this.supplier = supplier;
        this.registryKey = registryKey;
    }

    public Optional<T> getOptional() {
        Optional<Registry<T>> registryOptional = this.getRegistry();
        if (registryOptional.isPresent() && registryOptional.get() instanceof MappedRegistry<T> registry) {
            return registry.getOptional(this.location);
        }
        return Optional.empty();
    }

    public T get() {
        return this.getOptional().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public Optional<Registry<T>> getRegistry() {
        return (Optional<Registry<T>>) BuiltInRegistries.REGISTRY.getOptional(this.registryKey.location());
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    protected Supplier<T> getSupplier() {
        return this.supplier;
    }

    public boolean hasRegistered() {
        Optional<Registry<T>> registryOptional = this.getRegistry();
        if (registryOptional.isPresent() && registryOptional.get() instanceof MappedRegistry<T> registry) {
            return registry.containsKey(this.location);
        }
        return false;
    }

    protected void onRegister() {
        if(!this.hasRegistered()) throw new IllegalStateException("Object has not been registered");
        if(this.onRegister != null) this.onRegister.accept(this);
    }

    public void postRegister(Consumer<RegistryObject<T>> onRegister) {
        if(this.hasRegistered()) throw new IllegalStateException("Object already registered");
        this.onRegister = onRegister;
    }

}
