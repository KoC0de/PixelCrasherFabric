package de.pixelcrasher.command.defaults;

import de.pixelcrasher.command.Command;
import de.pixelcrasher.command.CommandExecutor;
import de.pixelcrasher.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Welcome! And Hello World");
        return true;
    }
}
