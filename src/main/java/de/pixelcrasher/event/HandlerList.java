package de.pixelcrasher.event;

import de.pixelcrasher.plugin.Plugin;
import de.pixelcrasher.plugin.RegisteredListener;

import java.util.*;

public class HandlerList {

    private static final ArrayList<HandlerList> allHandlers = new ArrayList<>();

    private volatile RegisteredListener[] handlers = null;

    private final EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerSlots;

    public HandlerList() {
        this.handlerSlots = new EnumMap<>(EventPriority.class);
        for(EventPriority priority : EventPriority.values()) {
            this.handlerSlots.put(priority, new ArrayList<>());
        }
        synchronized (allHandlers) {
            allHandlers.add(this);
        }
    }

    public synchronized void register(RegisteredListener listener) {
        if(this.handlerSlots.get(listener.getPriority()).contains(listener)) throw new IllegalStateException("This listener is already registered to priority " + listener.getPriority().toString());
        this.handlerSlots.get(listener.getPriority()).add(listener);
        handlers = null;
    }

    public void registerAll(Collection<RegisteredListener> listeners) {
        for(RegisteredListener listener : listeners) register(listener);
    }

    public synchronized void unregister(RegisteredListener listener) {
        if(this.handlerSlots.get(listener.getPriority()).remove(listener))
            this.handlers = null;
    }

    public synchronized void unregister(Plugin plugin) {
        boolean changed = false;
        for(List<RegisteredListener> list : this.handlerSlots.values()) {
            ListIterator<RegisteredListener> listIterator = list.listIterator();
            while(listIterator.hasNext()) {
                RegisteredListener listener = listIterator.next();
                if(listener.getPlugin().equals(plugin)) {
                    listIterator.remove();
                    changed = true;
                }
            }
        }
        if (changed) this.handlers = null;
    }

    public synchronized void unregister(Listener listener) {
        boolean changed = false;
        for(List<RegisteredListener> list : this.handlerSlots.values()) {
            ListIterator<RegisteredListener> listIterator = list.listIterator();
            while(listIterator.hasNext()) {
                RegisteredListener registeredListener = listIterator.next();
                if(registeredListener.getListener().equals(listener)) {
                    listIterator.remove();
                    changed = true;
                }
            }
        }
        if (changed) this.handlers = null;
    }

    public synchronized void bake() {
        if(this.handlers != null) return;
        List<RegisteredListener> entries = new ArrayList<>();
        for(Map.Entry<EventPriority, ArrayList<RegisteredListener>> entry : this.handlerSlots.entrySet())
            entries.addAll(entry.getValue());
        this.handlers = entries.toArray(new RegisteredListener[0]);
    }

    public RegisteredListener[] getRegisteredListeners() {
        this.bake();
        return this.handlers;
    }

    public static void bakeAll() {
        synchronized (allHandlers) {
            for(HandlerList h : allHandlers) h.bake();
        }
    }

    public static void unregisterAll() {
        synchronized (allHandlers) {
            for(HandlerList h : allHandlers) {
                for(List<RegisteredListener> list : h.handlerSlots.values()) {
                    list.clear();
                }
                h.handlers = null;
            }
        }
    }

    public static void unregisterAll(Plugin plugin) {
        synchronized (allHandlers) {
            for(HandlerList h : allHandlers) {
                h.unregister(plugin);
            }
        }
    }

    public static ArrayList<RegisteredListener> getRegisteredListeners(Plugin plugin) {
        ArrayList<RegisteredListener> listeners = new ArrayList<>();
        synchronized (allHandlers) {
            for (HandlerList handlerList : allHandlers) {
                for (List<RegisteredListener> list : handlerList.handlerSlots.values()) {
                    for (RegisteredListener listener : list) {
                        if (listener.getPlugin().equals(plugin))
                            listeners.add(listener);
                    }
                }
            }
        }
        return listeners;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<HandlerList> getHandlerLists() {
        synchronized (allHandlers) {
            return (ArrayList<HandlerList>) allHandlers.clone();
        }
    }

}
