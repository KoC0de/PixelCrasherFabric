package de.pixelcrasher.event.timing;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;

public class MinecraftTickEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
