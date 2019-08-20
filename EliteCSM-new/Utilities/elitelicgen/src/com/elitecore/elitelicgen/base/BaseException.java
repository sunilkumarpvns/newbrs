/*
 *  License Generation Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on Nov 6, 2007
 *  Created By kaushikvira
 */
package com.elitecore.elitelicgen.base;

/**
 * @author kaushikvira
 *
 */
public class BaseException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public BaseException() {}
    
    /**
     * @param message
     */
    public BaseException( String message ) {
        super(message);
    }
    
    /**
     * @param message
     * @param sourceField
     */
    
    /**
     * @param message
     * @param cause
     */
    public BaseException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    /**
     * @param cause
     */
    public BaseException( Throwable cause ) {
        super(cause);
        
    }
    
}
