package de.pixelcrasher.event.gui;

import de.pixelcrasher.event.Cancellable;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.gui.screens.Screen;

public class GuiScreenChangeEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private final Screen oldScreen;
    private Screen screen;

    public GuiScreenChangeEvent(Screen oldScreen, Screen screen) {
        this.oldScreen = oldScreen;
        this.screen = screen;
    }

    public Screen getOldScreen() {
        return this.oldScreen;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
