/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UnauthorizedAccessException.java                             
 * ModualName                                     
 * Created on Oct 10, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization;


/**
 * @author kaushikvira
 *
 */
public class UnidentifiedServerInstanceException extends AuthorizationException {
    
    /**
     * 
     */
    public UnidentifiedServerInstanceException() {
  
    }
    
    /**
     * @param message
     */
    public UnidentifiedServerInstanceException( String message ) {
        super(message);
       
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public UnidentifiedServerInstanceException( String message ,
                                        String sourceField ) {
        super(message, sourceField);
     
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnidentifiedServerInstanceException( String message ,
                                        Throwable cause ) {
        super(message, cause);
       
    }
    
    /**
     * @param cause
     */
    public UnidentifiedServerInstanceException( Throwable cause ) {
        super(cause);
     
    }
    
    public UnidentifiedServerInstanceException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
}
