package de.pixelcrasher.event.input;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;

public class CharacterTypeEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private final long windowId;

    private final int codepoint;
    private final int mods;

    public CharacterTypeEvent(long windowId, int codepoint, int mods) {
        this.windowId = windowId;
        this.codepoint = codepoint;
        this.mods = mods;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public long getWindowId() {
        return windowId;
    }

    public int getCodepoint() {
        return codepoint;
    }

    public int getMods() {
        return mods;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
