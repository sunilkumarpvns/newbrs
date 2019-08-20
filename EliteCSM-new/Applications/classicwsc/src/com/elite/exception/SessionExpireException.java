package com.elite.exception;

public class SessionExpireException extends Exception {
	
	public SessionExpireException() {
		super("Error during getting instance of Context_manager. Context_manager not initialized. ");
    }

    public SessionExpireException(String message) {
        super(message);
    }

    public SessionExpireException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionExpireException(Throwable cause) {
        super(cause);
    }
}
