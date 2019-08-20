/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   NullValueException.java                             
 * ModualName                                     
 * Created on Oct 8, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.elitesm.datamanager.core.exceptions.datavalidation;



/**
 * @author kaushikvira
 *
 */
public class NullValueException extends DataValidationException {
    
    /**
     * 
     */
    public NullValueException() {
   
    }
    
    /**
     * @param message
     */
    public NullValueException( String message ) {
        super(message);
       
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public NullValueException( String message ,
                                  String sourceField ) {
        super(message, sourceField);
    }  
    
    /**
     * @param message
     * @param cause
     */
    public NullValueException( String message ,
                                  Throwable cause ) {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public NullValueException( Throwable cause ) {
        super(cause);
       
    }
    
    public NullValueException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
}
