package de.pixelcrasher.event.net;

import de.pixelcrasher.event.Cancellable;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

public class PacketReceiveEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private final Connection connection;
    private final ChannelHandlerContext channelHandlerContext;
    private PacketListener packetListener;
    private Packet<?> packet;

    public PacketReceiveEvent(Connection connection, ChannelHandlerContext channelHandlerContext, Packet<?> packet, PacketListener packetListener) {
        this.connection = connection;
        this.channelHandlerContext = channelHandlerContext;
        this.packet = packet;
        this.packetListener = packetListener;
    }

    public PacketListener getPacketListener() {
        return packetListener;
    }

    public void setPacketListener(PacketListener packetListener) {
        this.packetListener = packetListener;
    }

    public Connection getConnection() {
        return connection;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
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
