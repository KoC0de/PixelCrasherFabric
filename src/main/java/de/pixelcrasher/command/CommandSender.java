package de.pixelcrasher.command;

import net.minecraft.network.chat.Component;

public interface CommandSender {

    void sendMessage(String message);
    void sendMessage(Component component);

}
