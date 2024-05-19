package de.pixelcrasher.command;

import de.pixelcrasher.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Command {

    private List<String> aliases;

    private String name;
    private String label;
    private String nextLabel;
    private String description;
    private String usageMessage;

    private CommandMap commandMap;

    private final Plugin providerPlugin;

    private CommandExecutor executor;

    protected Command(String name, Plugin plugin) {
        this(name, "", "#" + name, new ArrayList<>(), plugin);
    }

    protected Command(String name, String description, String usageMessage, List<String> aliases, Plugin provider) {
        this.name = name;
        this.nextLabel = name;
        this.label = name;
        this.description = description == null ? "" : description;
        this.usageMessage = usageMessage == null ? "#" + name : usageMessage;
        this.aliases = aliases;
        this.providerPlugin = provider;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        boolean success;
        if(!this.providerPlugin.isEnabled())
            throw new CommandException("Cannot execute command '" + label + "' in plugin " + this.providerPlugin.getDescription().getFullName() + " - plugin is disabled.");

        try {
            success = this.executor.onCommand(sender, this, label, args);
        } catch (Throwable e) {
            throw new CommandException("Unhandled exception executing command '" + label + "' in plugin " + this.providerPlugin.getDescription().getFullName(), e);
        }

        if(!success) sender.sendMessage(this.usageMessage);
        return success;
    }

    private boolean allowChangesFrom(CommandMap commandMap) {
        return !(this.commandMap != null && this.commandMap != commandMap);
    }

    public boolean register(CommandMap commandMap) {
        if (allowChangesFrom(commandMap)) {
            this.commandMap = commandMap;
            return true;
        }
        return false;
    }

    public boolean unregister(CommandMap commandMap) {
        if (allowChangesFrom(commandMap)) {
            this.commandMap = null;
            this.label = this.nextLabel;
            return true;
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public boolean setLabel(String label) {
        if (name == null)
            name = "";
        this.nextLabel = name;
        if (!isRegistered()) {
            this.label = name;
            return true;
        }
        return false;
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsage(String usageMessage) {
        this.usageMessage = usageMessage;
    }

    public boolean isRegistered() {
        return (this.commandMap != null);
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public Plugin getProvider() {
        return providerPlugin;
    }
}
