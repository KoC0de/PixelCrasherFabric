package de.pixelcrasher.event.chat;

import de.pixelcrasher.event.Cancellable;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.network.chat.LastSeenMessagesTracker;
import net.minecraft.network.chat.MessageSignature;

import java.time.Instant;

public class ChatMessageSendEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private String message;
    private Instant instant;
    private MessageSignature signature;
    private long saltSupplier;
    private LastSeenMessagesTracker.Update update;

    public ChatMessageSendEvent(String p249888, Instant instant, long i, MessageSignature messagesignature, LastSeenMessagesTracker.Update lastseenmessagestracker$update) {
        this.message = p249888;
        this.instant = instant;
        this.saltSupplier = i;
        this.signature = messagesignature;
        this.update = lastseenmessagestracker$update;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public long getSaltSupplier() {
        return saltSupplier;
    }

    public void setSaltSupplier(long saltSupplier) {
        this.saltSupplier = saltSupplier;
    }

    public MessageSignature getSignature() {
        return signature;
    }

    public void setSignature(MessageSignature signature) {
        this.signature = signature;
    }

    public LastSeenMessagesTracker.Update getUpdate() {
        return update;
    }

    public void setUpdate(LastSeenMessagesTracker.Update update) {
        this.update = update;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
