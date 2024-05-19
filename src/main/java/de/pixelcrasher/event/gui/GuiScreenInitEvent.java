package de.pixelcrasher.event.gui;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class GuiScreenInitEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private final Minecraft minecraft;
    private Screen screen;

    private final int width;
    private final int height;

    public GuiScreenInitEvent(Screen screen, Minecraft minecraft, int width, int height) {
        this.screen = screen;
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
