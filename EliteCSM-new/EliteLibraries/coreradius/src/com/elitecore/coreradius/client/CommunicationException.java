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

package com.elitecore.coreradius.client;

  
/**
 * This exception is thrown when there is any problem parsing raw EAP packet
 * data.
 */
public class CommunicationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CommunicationException(){
		super("Communication error.");
	}

    public CommunicationException(String message){
        super(message);
    }

    public CommunicationException(Throwable cause){
        super(cause);
    }

    public CommunicationException(String message, Throwable cause){
        super(message, cause);
    }
	
    
}
