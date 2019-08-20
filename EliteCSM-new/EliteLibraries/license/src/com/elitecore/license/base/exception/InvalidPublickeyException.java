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
public class InvalidPublickeyException extends InvalidLicenseException{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public InvalidPublickeyException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param arg0
     */
    public InvalidPublickeyException( String arg0 ) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param arg0
     */
    public InvalidPublickeyException( Throwable arg0 ) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param arg0
     * @param arg1
     */
    public InvalidPublickeyException( String arg0 , Throwable arg1 ) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    
}
