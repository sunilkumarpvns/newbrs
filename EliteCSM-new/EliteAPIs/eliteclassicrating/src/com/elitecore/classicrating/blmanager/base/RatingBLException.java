package com.elitecore.classicrating.blmanager.base;

public class RatingBLException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RatingBLException(){
	}
	
	public RatingBLException(String message){
		super(message);
	}

	public RatingBLException(String message, Throwable cause) {
		super(message, cause);
	}

	public RatingBLException(Throwable cause) {
		super(cause);
	}
	
	

}
