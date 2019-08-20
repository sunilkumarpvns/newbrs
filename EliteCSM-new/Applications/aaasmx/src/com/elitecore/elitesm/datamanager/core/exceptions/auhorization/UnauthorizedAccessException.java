/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UnauthorizedAccessException.java                             
 * ModualName                                     
 * Created on Oct 10, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.elitesm.datamanager.core.exceptions.auhorization;


/**
 * @author kaushikvira
 *
 */
public class UnauthorizedAccessException extends AuthorizationException {
    
    /**
     * 
     */
    public UnauthorizedAccessException() {
  
    }
    
    /**
     * @param message
     */
    public UnauthorizedAccessException( String message ) {
        super(message);
       
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public UnauthorizedAccessException( String message ,
                                        String sourceField ) {
        super(message, sourceField);
     
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnauthorizedAccessException( String message ,
                                        Throwable cause ) {
        super(message, cause);
       
    }
    
    /**
     * @param cause
     */
    public UnauthorizedAccessException( Throwable cause ) {
        super(cause);
     
    }
    
    public UnauthorizedAccessException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
}
