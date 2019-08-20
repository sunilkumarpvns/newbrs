/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   FormatInvalidException.java                             
 * ModualName                                     
 * Created on Oct 10, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation;



/**
 * @author kaushikvira
 *
 */
public class FormatInvalidException extends DataValidationException {
    
    /**
     * 
     */
    public FormatInvalidException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public FormatInvalidException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public FormatInvalidException( String message ,
                                   String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public FormatInvalidException( String message ,
                                   Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public FormatInvalidException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    
    public FormatInvalidException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
}
