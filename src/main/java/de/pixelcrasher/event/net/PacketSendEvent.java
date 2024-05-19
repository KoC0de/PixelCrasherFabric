package de.pixelcrasher.event.net;

import de.pixelcrasher.event.Cancellable;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.Nullable;

public class PacketSendEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private final Connection connection;
    private final Channel channel;

    private Packet<?> packet;
    private final PacketSendListener listener;
    private final ConnectionProtocol packetProtocol;
    private final ConnectionProtocol currentProtocol;

    public PacketSendEvent(Connection connection, Channel channel, Packet<?> packet, @Nullable PacketSendListener listener, ConnectionProtocol packetProtocol, ConnectionProtocol currentProtocol) {
        this.connection = connection;
        this.channel = channel;

        this.packet = packet;
        this.listener = listener;

        this.packetProtocol = packetProtocol;
        this.currentProtocol = currentProtocol;
    }

    public Connection getConnection() {
        return connection;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public PacketSendListener getListener() {
        return listener;
    }

    public ConnectionProtocol getCurrentProtocol() {
        return currentProtocol;
    }

    public ConnectionProtocol getPacketProtocol() {
        return packetProtocol;
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
