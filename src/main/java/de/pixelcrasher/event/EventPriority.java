package de.pixelcrasher.event;

public enum EventPriority {

    LOWEST(0),
    NORMAL(1),
    HIGH(2),
    HIGHEST(3),
    MONITOR(4);

    private final int slot;

    EventPriority(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
