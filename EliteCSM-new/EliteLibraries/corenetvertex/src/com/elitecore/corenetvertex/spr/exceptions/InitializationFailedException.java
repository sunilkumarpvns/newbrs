package com.elitecore.corenetvertex.spr.exceptions;

public class InitializationFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public InitializationFailedException() {
	super("Failed Initialization.");
    }

    public InitializationFailedException(String message) {
	super(message);
    }

    public InitializationFailedException(String message, Throwable cause) {
	super(message, cause);
    }

    public InitializationFailedException(Throwable cause) {
	super(cause);
    }

}
