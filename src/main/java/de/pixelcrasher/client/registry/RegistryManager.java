package de.pixelcrasher.client.registry;

import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.event.core.RegistryInitEvent;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class RegistryManager {

    private final PixelCrasher clientManager;

    public RegistryManager(PixelCrasher clientManager) {
        this.clientManager = clientManager;
    }

    public void register() {

        this.clientManager.getPluginManager().call(new RegistryInitEvent(BuiltInRegistries.REGISTRY));

    }

}
