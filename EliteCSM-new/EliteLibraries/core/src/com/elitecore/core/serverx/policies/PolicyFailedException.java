package com.elitecore.core.serverx.policies;

public class PolicyFailedException extends Exception {
	private static final long serialVersionUID = 1L;

	public PolicyFailedException() {
		super("Policy Failed Exception");
	}

	public PolicyFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PolicyFailedException(String message) {
		super(message);
	}

	public PolicyFailedException(Throwable cause) {
		super(cause);
	}
}
