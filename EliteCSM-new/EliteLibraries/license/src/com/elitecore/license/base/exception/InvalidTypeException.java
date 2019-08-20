/*
 *  License Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on Oct 25, 2007
 *  Created By kaushikvira
 */
package com.elitecore.license.base.exception;

import com.elitecore.license.base.exception.licensetype.InvalidLicenseException;


/**
 * @author kaushikvira
 *
 */
public class InvalidTypeException extends InvalidLicenseException {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public InvalidTypeException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public InvalidTypeException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public InvalidTypeException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public InvalidTypeException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
}
