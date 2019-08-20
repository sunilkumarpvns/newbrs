package com.elitecore.core.serverx.policies;

public class PolicyNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public PolicyNotFoundException() {
		super("Policy Failed Exception");
	}

	public PolicyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PolicyNotFoundException(String message) {
		super(message);
	}

	public PolicyNotFoundException(Throwable cause) {
		super(cause);
	}
}
