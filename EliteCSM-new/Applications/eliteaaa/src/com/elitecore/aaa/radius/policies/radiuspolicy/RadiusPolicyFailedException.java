package com.elitecore.aaa.radius.policies.radiuspolicy;

public class RadiusPolicyFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8586402702003976468L;

	public RadiusPolicyFailedException() {
		super("Radius Policy Failed.");
	}

	public RadiusPolicyFailedException(String message) {
		super(message);
	}

	public RadiusPolicyFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RadiusPolicyFailedException(Throwable cause) {
		super(cause);
	}

}
