package de.pixelcrasher.event.input;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;

public class KeyEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    public final static int KEY_RELEASE = 0;
    public final static int KEY_HOLD = 2;
    public final static int KEY_PRESS = 1;


    private final long windowId;

    private final int key;
    private final int scanCode;
    private final int eventType; // 0 - release; 1 - press; 2 - long press
    private final int mods;

    public KeyEvent(long windowId, int key, int scanCode, int eventType, int mods) {
        this.windowId = windowId;
        this.key = key;
        this.scanCode = scanCode;
        this.eventType = eventType;
        this.mods = mods;
    }

    public long getWindowId() {
        return windowId;
    }

    public int getMods() {
        return mods;
    }

    public int getEventType() {
        return eventType;
    }

    public int getKey() {
        return key;
    }

    public int getScanCode() {
        return scanCode;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
