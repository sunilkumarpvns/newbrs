package com.elitecore.coreeap.packet;

public class EAPException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public EAPException() {

		super("EAP Exception");
	}

	public EAPException(String message) {
		super(message);		
	}

	public EAPException(Throwable cause) {
		super(cause);
	}

	public EAPException(String message, Throwable cause) {
		super(message, cause);
	}

}
