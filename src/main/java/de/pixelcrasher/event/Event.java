package de.pixelcrasher.event;

public abstract class Event {

    private String name;
    private final boolean async;

    public Event() {
        this(false);
    }

    public Event(boolean async) {
        this.async = async;
    }

    public String getEventName() {
        if(this.name == null) return this.getClass().getSimpleName();
        return this.name;
    }

    public abstract HandlerList getHandlers();

    public final boolean isAsynchronous() {
        return this.async;
    }

    public enum Result {

        DENY, DEFAULT, ALLOW;

    }

}
