package de.pixelcrasher.command.defaults;

import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.command.Command;
import de.pixelcrasher.command.CommandExecutor;
import de.pixelcrasher.command.CommandSender;
import de.pixelcrasher.plugin.Plugin;
import de.pixelcrasher.plugin.internal.CorePluginLoaderAdapter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class PluginCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Plugin[] plugins = PixelCrasher.getInstance().getPluginManager().getPlugins();
        if (plugins.length == 0) {
            sender.sendMessage("§8[§cAddonManager§8] §7No addons loaded!");
            return true;
        }

        MutableComponent message = Component.empty().append("§8[§cAddonManager§8] §7");

        for (int i = 0; i < plugins.length; i++) {
            Plugin plugin = plugins[i];

            MutableComponent pluginHover = Component.empty().append(plugin.getDescription().getFullName());
            if(!plugin.getDescription().getAuthors().isEmpty())
                pluginHover.append("\n\nAuthors:\n- " + String.join("\n- ", plugin.getDescription().getAuthors()));
            if(!plugin.getDescription().getDepend().isEmpty())
                pluginHover.append("\n\nDependencies:\n- " + String.join("\n- ", plugin.getDescription().getDepend()));

            if(PixelCrasher.getInstance().getConfig().getBoolean("developer.debug.showLoaders")) {
                pluginHover.append("\n\n§cPlugin Loader: " + plugin.getPluginLoader().getClass().getSimpleName());
                pluginHover.append("\n§cClass Loader: " + plugin.getClass().getClassLoader().getClass().getSimpleName());
            }

            Component pluginMessage = Component.empty().append((plugin.getPluginLoader() instanceof CorePluginLoaderAdapter ? "§e" : plugin.isEnabled() ? "§a" : "§c") + plugin.getName()).setStyle(Style.EMPTY
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, pluginHover)));

            message.append(pluginMessage);
            if (i != plugins.length - 1)
                message.append("§7, ");
        }
        sender.sendMessage(message);
        return true;
    }
}
