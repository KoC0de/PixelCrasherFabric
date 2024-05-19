package de.pixelcrasher.event.gui;

import de.pixelcrasher.event.Cancellable;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;

public class GuiOverlayRenderEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private final Minecraft minecraft;
    private final Overlay overlay;
    private final GuiGraphics guiGraphics;
    private final int mouseX, mouseY;
    private final float deltaFrameTime;

    private boolean cancelled = false;

    public GuiOverlayRenderEvent(Minecraft minecraft, Overlay overlay, GuiGraphics guigraphics, int i, int j, float deltaFrameTime) {
        this.minecraft = minecraft;
        this.overlay = overlay;
        this.guiGraphics = guigraphics;
        this.mouseX = i;
        this.mouseY = j;
        this.deltaFrameTime = deltaFrameTime;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public Overlay getOverlay() {
        return this.overlay;
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
