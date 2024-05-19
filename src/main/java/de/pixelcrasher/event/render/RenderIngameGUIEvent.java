package de.pixelcrasher.event.render;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.gui.GuiGraphics;

public class RenderIngameGUIEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private final float partialTicks;

    private final GuiGraphics guiGraphics;

    public RenderIngameGUIEvent(GuiGraphics guiGraphics, float partialTicks) {
        this.guiGraphics = guiGraphics;
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
