package de.pixelcrasher.plugin;

public class InvalidDescriptionException extends Exception {

    public InvalidDescriptionException() {}

    public InvalidDescriptionException(String message) {
        super(message);
    }

    public InvalidDescriptionException(Exception message) {
        super(message);
    }

}
