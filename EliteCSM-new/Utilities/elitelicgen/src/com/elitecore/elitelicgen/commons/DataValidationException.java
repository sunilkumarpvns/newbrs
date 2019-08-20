/*
 *  License Generation Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on Nov 19, 2007
 *  Created By kaushikvira
 */
package com.elitecore.elitelicgen.commons;


/**
 * @author kaushikvira
 *
 */
public class DataValidationException extends EliteLicGenException {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public DataValidationException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public DataValidationException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public DataValidationException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public DataValidationException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
}
