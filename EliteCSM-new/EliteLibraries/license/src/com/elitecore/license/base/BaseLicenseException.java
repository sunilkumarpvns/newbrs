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
package com.elitecore.license.base;


/**
 * @author kaushikvira
 *
 */
public class BaseLicenseException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public BaseLicenseException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param arg0
     */
    public BaseLicenseException( String arg0 ) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param arg0
     */
    public BaseLicenseException( Throwable arg0 ) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param arg0
     * @param arg1
     */
    public BaseLicenseException( String arg0 , Throwable arg1 ) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    
}
