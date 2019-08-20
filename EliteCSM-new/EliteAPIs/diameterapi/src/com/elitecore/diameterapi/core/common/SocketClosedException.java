package com.elitecore.diameterapi.core.common;

public class SocketClosedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SocketClosedException() {
		super("Client Socket Closed");
	}

	public SocketClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	public SocketClosedException(String message) {
		super(message);
	}

	public SocketClosedException(Throwable cause) {
		super(cause);
	}

	
	
}
