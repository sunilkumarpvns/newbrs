/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 2nd February 2007
 *  
 */

package com.elitecore.coreeap.packet;

 
/**
 * This exception is thrown when there is any problem parsing raw EAP packet
 * data.
 */
public class InvalidEAPPacketException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidEAPPacketException(){
		super("Invalid attribute length.");
	}

    public InvalidEAPPacketException(String message){
        super(message);
    }

    public InvalidEAPPacketException(Throwable cause){
        super(cause);
    }

    public InvalidEAPPacketException(String message, Throwable cause){
        super(message, cause);
    }
	
    
}
