package de.pixelcrasher.plugin;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.EventException;
import de.pixelcrasher.event.Listener;

public interface EventExecutor {

    void execute(Listener paramListener, Event paramEvent) throws EventException;

}
