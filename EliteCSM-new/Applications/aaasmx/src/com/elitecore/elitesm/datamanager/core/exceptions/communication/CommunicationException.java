/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CommunicationException.java                             
 * ModualName                                     
 * Created on Oct 8, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.elitesm.datamanager.core.exceptions.communication;

import com.elitecore.elitesm.datamanager.DataManagerException;


/**
 * @author kaushikvira
 *
 */
public class CommunicationException extends DataManagerException {
    
    /**
     * 
     */
    public CommunicationException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public CommunicationException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public CommunicationException( String message ,
                                   String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public CommunicationException( String message ,
                                   Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public CommunicationException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    public CommunicationException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
}
