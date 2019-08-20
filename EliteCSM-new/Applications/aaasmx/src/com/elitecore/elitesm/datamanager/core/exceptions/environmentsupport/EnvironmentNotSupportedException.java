/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   EnvironmentNotSupportedException.java                             
 * ModualName                                     
 * Created on Oct 8, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport;

import com.elitecore.elitesm.datamanager.DataManagerException;

/**
 * @author kaushikvira
 */
public class EnvironmentNotSupportedException extends DataManagerException {
    
    /**
     * 
     */
    public EnvironmentNotSupportedException() {
      }
    
    /**
     * @param message
     */
    public EnvironmentNotSupportedException( String message ) {
        super(message);
        
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public EnvironmentNotSupportedException( String message ,
                                             String sourceField ) {
        super(message, sourceField);
        
    }
    
    /**
     * @param message
     * @param cause
     */
    public EnvironmentNotSupportedException( String message ,
                                             Throwable cause ) {
        super(message, cause);
        
    }
    
    /**
     * @param cause
     */
    public EnvironmentNotSupportedException( Throwable cause ) {
        super(cause);
        
    }
    public EnvironmentNotSupportedException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
    
}
