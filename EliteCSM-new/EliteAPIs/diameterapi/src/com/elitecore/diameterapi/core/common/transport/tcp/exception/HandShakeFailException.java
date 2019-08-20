package com.elitecore.diameterapi.core.common.transport.tcp.exception;


public class HandShakeFailException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HandShakeFailException(String message) {
		super(message);
	    }

	
	    public HandShakeFailException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public HandShakeFailException(Throwable cause) {
	        super(cause);
	    }
	
	

}
