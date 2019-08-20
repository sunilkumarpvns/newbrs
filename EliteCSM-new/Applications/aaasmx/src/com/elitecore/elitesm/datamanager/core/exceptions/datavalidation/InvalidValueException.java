/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InvalidValueException.java                             
 * ModualName                                     
 * Created on Oct 10, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.elitesm.datamanager.core.exceptions.datavalidation;



/**
 * @author kaushikvira
 *
 */
public class InvalidValueException extends DataValidationException {
    
    /**
     * 
     */
    public InvalidValueException() {
   
    }
    
    /**
     * @param message
     */
    public InvalidValueException( String message ) {
        super(message);
       
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public InvalidValueException( String message ,
                                  String sourceField ) {
        super(message, sourceField);
     
    }
    
    /**
     * @param message
     * @param cause
     */
    public InvalidValueException( String message ,
                                  Throwable cause ) {
        super(message, cause);
      
    }
    
    /**
     * @param cause
     */
    public InvalidValueException( Throwable cause ) {
        super(cause);
     
    }
    public InvalidValueException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
    
}
