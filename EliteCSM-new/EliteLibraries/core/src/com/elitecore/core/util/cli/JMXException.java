/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 19th November 2007
 *  Ezhava Baiju D
 *  
 */


package com.elitecore.core.util.cli;

 
public class JMXException extends Exception {

	private static final long serialVersionUID = 1L;

	public JMXException() {
		super("Error during initialization of JMX interface.");
    }
   
    public JMXException(String message) {
        super(message);    
    }
   
    public JMXException(String message, Throwable cause) {
        super(message, cause);    
    }
   
    public JMXException(Throwable cause) {
        super(cause);    
    }
    
}
