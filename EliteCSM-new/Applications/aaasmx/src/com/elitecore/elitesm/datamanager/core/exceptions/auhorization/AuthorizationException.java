/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   AuthorizationException.java                             
 * ModualName                                     
 * Created on Oct 8, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.elitesm.datamanager.core.exceptions.auhorization;

import com.elitecore.elitesm.datamanager.DataManagerException;


/**
 * @author kaushikvira
 *
 */
public class AuthorizationException extends DataManagerException {
    
    /**
     * 
     */
    public AuthorizationException() {   
    }
    
    /**
     * @param message
     */
    public AuthorizationException( String message ) {
        super(message);
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public AuthorizationException( String message ,
                                   String sourceField ) {
        super(message, sourceField);
    
    }
    
    /**
     * @param message
     * @param cause
     */
    public AuthorizationException( String message ,
                                   Throwable cause ) {
        super(message, cause);
       
    }
    
    /**
     * @param cause
     */
    public AuthorizationException( Throwable cause ) {
        super(cause);
      
    }
    
    public AuthorizationException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
}
