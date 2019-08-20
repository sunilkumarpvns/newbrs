package com.elitecore.coreeap.packet.types.tls;

public class TLSException extends Exception{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TLSException(){
		super("TLS Error");
	}
	
	public TLSException(String message){
		super(message);
	}
	
	public TLSException(Throwable cause){
		super(cause);
	}
	
	public TLSException(String message, Throwable cause){
		super(message, cause);
	}
	
}
