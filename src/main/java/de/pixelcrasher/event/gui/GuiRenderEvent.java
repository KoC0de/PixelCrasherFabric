package de.pixelcrasher.event.gui;

import de.pixelcrasher.event.Cancellable;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class GuiRenderEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private final Minecraft minecraft;
    private final Screen screen;
    private final GuiGraphics guiGraphics;
    private final int mouseX, mouseY;
    private final float deltaFrameTime;

    private boolean cancelled = false;

    public GuiRenderEvent(Minecraft minecraft, Screen screen, GuiGraphics guigraphics, int i, int j, float deltaFrameTime) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.guiGraphics = guigraphics;
        this.mouseX = i;
        this.mouseY = j;
        this.deltaFrameTime = deltaFrameTime;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public float getDeltaFrameTime() {
        return deltaFrameTime;
    }

    public int getWidth() {
        return this.screen.width;
    }

    public int getHeight() {
        return this.screen.height;
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
