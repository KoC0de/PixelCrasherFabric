package de.pixelcrasher.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class ClientPlayer implements IClientPlayer {

    @Override
    public void sendMessage(String message) {
        this.sendMessage(Component.literal(message));
    }

    @Override
    public void sendMessage(Component component) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;
        player.displayClientMessage(component, false);
    }

    @Override
    public void chat(String message) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;
        player.connection.sendChat(message);
    }
}
