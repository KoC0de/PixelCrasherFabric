package de.pixelcrasher.event.gui;

import de.pixelcrasher.event.Cancellable;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class GuiWidgetFocusEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private boolean cancelled = false;


    private final GuiEventListener focus;
    private GuiEventListener widget;

    public GuiWidgetFocusEvent(GuiEventListener focused, GuiEventListener widget) {
        this.focus = focused;
        this.widget = widget;
    }

    public GuiEventListener getWidget() {
        return widget;
    }

    public void setWidget(GuiEventListener widget) {
        this.widget = widget;
    }

    public GuiEventListener getFocused() {
        return focus;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
