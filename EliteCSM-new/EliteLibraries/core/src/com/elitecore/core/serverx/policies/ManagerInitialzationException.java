package com.elitecore.core.serverx.policies;

public class ManagerInitialzationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7107635886379644200L;

	public ManagerInitialzationException() {
		super("Error during manager initialization");
	}

	public ManagerInitialzationException(String message) {
		super(message);
	}

	public ManagerInitialzationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ManagerInitialzationException(Throwable cause) {
		super(cause);
	}

}
