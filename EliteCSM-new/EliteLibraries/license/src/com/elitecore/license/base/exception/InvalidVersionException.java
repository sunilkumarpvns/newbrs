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
public class InvalidVersionException extends InvalidLicenseException {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public InvalidVersionException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public InvalidVersionException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public InvalidVersionException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public InvalidVersionException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
}
