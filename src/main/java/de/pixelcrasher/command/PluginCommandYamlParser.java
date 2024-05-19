package de.pixelcrasher.command;

import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PluginCommandYamlParser {

    public static List<Command> parse(Plugin plugin) {
        List<Command> commands = new ArrayList<>();
        Map<String, Map<String, Object>> map = plugin.getDescription().getCommands();
        if(map == null) return commands;
        for(Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            if(entry.getKey().contains(":")) {
                PixelCrasher.getInstance().getLogger().error("Could not load command " + entry.getKey() + " for plugin " + plugin.getName() + ": Illegal Characters");
                continue;
            }

            Command command = new Command(entry.getKey(), plugin);
            Object description = entry.getValue().get("description");
            Object usage = entry.getValue().get("usage");
            Object aliases = entry.getValue().get("aliases");

            if(description != null) command.setDescription(description.toString());
            if(usage != null) command.setUsage(usage.toString());
            if(aliases != null) {
                List<String> aliasList = new ArrayList<>();
                if(aliases instanceof List) {
                    for(Object o : (List<?>)aliases) {
                        if (o.toString().contains(":")) {
                            PixelCrasher.getInstance().getLogger().error("Could not load alias " + o + " for plugin " + plugin.getName() + ": Illegal Characters");
                            continue;
                        }
                        aliasList.add(o.toString());
                    }
                } else if (aliases.toString().contains(":")) {
                    PixelCrasher.getInstance().getLogger().error("Could not load alias " + aliases + " for plugin " + plugin.getName() + ": Illegal Characters");
                } else {
                    aliasList.add(aliases.toString());
                }
                command.setAliases(aliasList);
            }
            commands.add(command);
        }
        return commands;
    }

}
