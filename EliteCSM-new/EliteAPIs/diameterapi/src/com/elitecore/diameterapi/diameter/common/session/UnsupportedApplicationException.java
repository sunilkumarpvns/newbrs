package com.elitecore.diameterapi.diameter.common.session;


public class UnsupportedApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final long unsupportedApplicationId;

	public UnsupportedApplicationException(long appId) {
		this.unsupportedApplicationId = appId;
	}

	public long getUnsupportedApplicationId() {
		return unsupportedApplicationId;
	}
	
}
