package com.elite.exception;

public class ContextManagerNotInitialisedException extends Exception {
	
	public ContextManagerNotInitialisedException() {
		super("Error during getting instance of Context_manager. Context_manager not initialized. ");
    }

    public ContextManagerNotInitialisedException(String message) {
        super(message);
    }

    public ContextManagerNotInitialisedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextManagerNotInitialisedException(Throwable cause) {
        super(cause);
    }
}
