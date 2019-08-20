package com.elitecore.aaa.radius.policies.radiuspolicy;
 
public class RadiusPolicyCheckItemFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public RadiusPolicyCheckItemFailedException() {
		super("Radius Policy check item failed");
	}

	public RadiusPolicyCheckItemFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RadiusPolicyCheckItemFailedException(String message) {
		super(message);
	}

	public RadiusPolicyCheckItemFailedException(Throwable cause) {
		super(cause);
	}

}
