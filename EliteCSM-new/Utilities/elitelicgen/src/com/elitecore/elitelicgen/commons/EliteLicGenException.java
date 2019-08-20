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
package com.elitecore.elitelicgen.commons;

import com.elitecore.elitelicgen.base.BaseException;


/**
 * @author kaushikvira
 *
 */
public class EliteLicGenException extends BaseException {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public EliteLicGenException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public EliteLicGenException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public EliteLicGenException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public EliteLicGenException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
}
