package de.pixelcrasher.util.player;

import de.pixelcrasher.command.CommandSender;

public interface IClientPlayer extends CommandSender {

    void chat(String message);

}
