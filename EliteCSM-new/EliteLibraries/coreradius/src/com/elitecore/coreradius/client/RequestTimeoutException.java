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
public class RequestTimeoutException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public RequestTimeoutException(){
		super("Request timeout.");
	}

    public RequestTimeoutException(String message){
        super(message);
    }

    public RequestTimeoutException(Throwable cause){
        super(cause);
    }

    public RequestTimeoutException(String message, Throwable cause){
        super(message, cause);
    }
	
    
}
