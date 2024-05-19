package de.pixelcrasher.plugin;

public class UnknownDependencyException extends RuntimeException {

    public UnknownDependencyException() {}
    public UnknownDependencyException(String msg) {
        super(msg);
    }


}
