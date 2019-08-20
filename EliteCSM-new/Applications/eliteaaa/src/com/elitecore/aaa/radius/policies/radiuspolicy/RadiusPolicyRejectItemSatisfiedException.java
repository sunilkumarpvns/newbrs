package com.elitecore.aaa.radius.policies.radiuspolicy;
 
public class RadiusPolicyRejectItemSatisfiedException extends Exception {

	private static final long serialVersionUID = 1L;

	public RadiusPolicyRejectItemSatisfiedException() {
		super("Radius Policy Reject item satisfied");
	}

	public RadiusPolicyRejectItemSatisfiedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RadiusPolicyRejectItemSatisfiedException(String message) {
		super(message);
	}

	public RadiusPolicyRejectItemSatisfiedException(Throwable cause) {
		super(cause);
	}

}
