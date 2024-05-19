package de.pixelcrasher.event;

public interface Cancellable {

    boolean isCancelled();
    void setCancelled(boolean cancelled);

}
