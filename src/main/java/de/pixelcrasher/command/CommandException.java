package de.pixelcrasher.command;

public class CommandException extends RuntimeException {

    public CommandException() {
        super();
    }

    public CommandException(String msg) {
        super(msg);
    }

    public CommandException(Throwable msg) {
        super(msg);
    }
    public CommandException(String msg, Throwable e) {
        super(msg, e);
    }

}
