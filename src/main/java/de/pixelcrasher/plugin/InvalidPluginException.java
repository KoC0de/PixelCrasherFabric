package de.pixelcrasher.plugin;

public class InvalidPluginException extends Exception {

    public InvalidPluginException() {}
    public InvalidPluginException(String message) {
        super(message);
    }

    public InvalidPluginException(Throwable message) {
        super(message);
    }
    public InvalidPluginException(String message, Throwable ex) {
        super(message, ex);
    }


}
