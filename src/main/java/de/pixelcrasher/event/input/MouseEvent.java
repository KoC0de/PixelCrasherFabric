package de.pixelcrasher.event.input;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;

public class MouseEvent extends Event {

    private final static HandlerList handlers = new HandlerList();


    public final static int MOUSE_RELEASE = 0;
    public final static int MOUSE_PRESS = 1;

    public final static int BUTTON_LEFT = 0;
    public final static int BUTTON_MIDDLE = 2;
    public final static int BUTTON_RIGHT = 1;


    private final long windowId;
    private final int button;
    private final int state;
    private final int p91534;

    public MouseEvent(long windowId, int button, int state, int p91534) {
        this.windowId = windowId;
        this.button = button;
        this.state = state;
        this.p91534 = p91534;
    }

    public int getButton() {
        return button;
    }

    public int getState() {
        return state;
    }

    public long getWindowId() {
        return windowId;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
