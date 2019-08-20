/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 30th October 2007
 *  Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.core.systemx.esix.udp;


public class SessionLimitReachedException extends Exception {

	private static final long serialVersionUID = 1L;

	public SessionLimitReachedException() {
		super("Session limit reached.");
    }
   
    public SessionLimitReachedException(String message) {
        super(message);    
    }
   
    public SessionLimitReachedException(String message, Throwable cause) {
        super(message, cause);    
    }
   
    public SessionLimitReachedException(Throwable cause) {
        super(cause);    
    }
    
}
