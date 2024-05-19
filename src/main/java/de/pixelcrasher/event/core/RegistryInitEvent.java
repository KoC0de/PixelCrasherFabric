package de.pixelcrasher.event.core;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.core.Registry;

public class RegistryInitEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private final Registry<? extends Registry<?>> registry;

    public RegistryInitEvent(Registry<? extends Registry<?>> root) {
        this.registry = root;
    }

    public Registry<? extends Registry<?>> getRoot() {
        return this.registry;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
