/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ValueOutOfRangeException.java                             
 * ModualName                                     
 * Created on Oct 10, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation;


/**
 * @author kaushikvira
 */
public class ValueOutOfRangeException extends DataValidationException {
    
    /**
     * 
     */
    public ValueOutOfRangeException() {

    }
    
    /**
     * @param message
     */
    public ValueOutOfRangeException( String message ) {
        super(message);
        
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public ValueOutOfRangeException( String message ,
                                     String sourceField ) {
        super(message, sourceField);
        
    }
    
    /**
     * @param message
     * @param cause
     */
    public ValueOutOfRangeException( String message ,
                                     Throwable cause ) {
        super(message, cause);
        
    }
    
    /**
     * @param cause
     */
    public ValueOutOfRangeException( Throwable cause ) {
        super(cause);
        
    }
    
    
    public ValueOutOfRangeException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
}
