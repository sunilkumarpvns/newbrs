package com.elitecore.netvertex.gateway.diameter.af;

public class MissingAPVException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1310868952337410828L;
	
	private final String avpId;


	public MissingAPVException(String avpId) {
		super(avpId + " is missing in packet");
		this.avpId = avpId;
	}

	public String getAvpId() {
		return avpId;
	}
}
