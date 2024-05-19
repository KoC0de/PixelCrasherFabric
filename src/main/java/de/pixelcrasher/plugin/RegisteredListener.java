package de.pixelcrasher.plugin;

import de.pixelcrasher.event.*;

public class RegisteredListener {

    private final Listener listener;
    private final EventPriority priority;
    private final Plugin plugin;
    private final EventExecutor executor;
    private final boolean ignoreCancelled;

    public RegisteredListener(Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        this.listener = listener;
        this.executor = executor;
        this.priority = priority;
        this.plugin = plugin;
        this.ignoreCancelled = ignoreCancelled;
    }

    public Listener getListener() {
        return this.listener;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public void callEvent(Event event) throws EventException {
        if(event instanceof Cancellable && ((Cancellable)event).isCancelled() && this.ignoreCancelled) return;
        this.executor.execute(this.listener, event);
    }

    public boolean isIgnoreCancelled() {
        return ignoreCancelled;
    }

    @Override
    public String toString() {
        return "[provider=" + this.plugin.getName() + ",executor=" + this.executor + "]";
    }
}
