package com.elite.exception;

public class CommunicationException extends Exception {

	public CommunicationException() {
		super("Error during communicating with server.");
    }

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicationException(Throwable cause) {
        super(cause);
    }

}
