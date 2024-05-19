package de.pixelcrasher.command;

import java.util.List;

public interface CommandMap {

    void registerAll(String fallbackPrefix, List<Command> commands);

    boolean register(String fallbackPrefix, Command command);
    boolean register(String label, String fallbackPrefix, Command command);

    boolean dispatch(CommandSender sender, String commandLine) throws CommandException;

    void clearCommands();

    Command getCommand(String command);

}
