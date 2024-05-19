package de.pixelcrasher.command.defaults;

import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.command.Command;
import de.pixelcrasher.command.CommandExecutor;
import de.pixelcrasher.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§8[§6CORE§8] §cThe core plugin will reload.");
        PixelCrasher.getInstance().reload();
        sender.sendMessage("§8[§6CORE§8] §cThe core plugin has been reloaded!");
        return true;
    }
}
