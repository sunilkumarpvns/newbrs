package com.elitecore.coreeap.packet;

public class InvalidEAPTypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidEAPTypeException(){
		super("Invalid EAP Type");
	}
 
    public InvalidEAPTypeException(String message){
        super(message);
    }

    public InvalidEAPTypeException(Throwable cause){
        super(cause);
    }

    public InvalidEAPTypeException(String message, Throwable cause){
        super(message, cause);
    }
}
