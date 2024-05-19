package de.pixelcrasher.command;

import de.pixelcrasher.PixelCrasher;

import java.util.*;

public class SimpleCommandMap implements CommandMap {

    private final Map<String, Command> commands = new HashMap<>();

    public SimpleCommandMap(PixelCrasher clientManager) {}

    @Override
    public void registerAll(String fallbackPrefix, List<Command> commands) {
        if(commands != null)
            for(Command command : commands) register(fallbackPrefix, command);
    }

    @Override
    public boolean register(String fallbackPrefix, Command command) {
        return register(command.getName(), fallbackPrefix, command);
    }

    @Override
    public boolean register(String label, String fallbackPrefix, Command command) {
        label = label.toLowerCase(Locale.ENGLISH).trim();
        fallbackPrefix = fallbackPrefix.toLowerCase(Locale.ENGLISH).trim();
        boolean registered = register(label, command, false, fallbackPrefix);
        Iterator<String> iterator = command.getAliases().iterator();
        while(iterator.hasNext()) {
            if(!register(iterator.next(), command, true, fallbackPrefix))
                iterator.remove();
        }
        if(!registered)
            command.setLabel(fallbackPrefix + ":" + label);
        command.register(this);
        return false;
    }

    private synchronized boolean register(String label, Command command, boolean alias, String prefix) {
        this.commands.put(prefix + ":" + label, command);
        if(alias && this.commands.containsKey(label)) return false;
        Command conflict = this.commands.get(label);
        if(conflict != null && conflict.getLabel().equals(label)) return false;
        if(!alias) command.setLabel(label);
        this.commands.put(label, command);
        return true;
    }

    @Override
    public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
        String[] args = commandLine.split(" ");
        if(args.length == 0) return false;
        String sentLabel = args[0].toLowerCase(Locale.ENGLISH);
        Command target = getCommand(sentLabel);

        if(target == null) {
            sender.sendMessage("§8[§cERROR§8] §7The command could not be found! Type '#help' for help.");
            return false;
        }

        try {
            return target.execute(sender, sentLabel, Arrays.copyOfRange(args, 1, args.length));
        } catch (CommandException e) {
            throw e;
        } catch (Throwable e) {
            throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, e);
        }
    }

    @Override
    public synchronized void clearCommands() {
        for (Map.Entry<String, Command> entry : this.commands.entrySet())
            (entry.getValue()).unregister(this);
        this.commands.clear();
    }

    @Override
    public Command getCommand(String command) {
        return this.commands.get(command.toLowerCase(Locale.ENGLISH));
    }

    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(this.commands.values());
    }

}
