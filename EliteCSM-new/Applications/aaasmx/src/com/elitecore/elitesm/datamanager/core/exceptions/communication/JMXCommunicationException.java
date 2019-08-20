/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   JMXCommunicationException.java                             
 * ModualName                                     
 * Created on Nov 24, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.elitesm.datamanager.core.exceptions.communication;


/**
 * @author kaushikvira
 *
 */
public class JMXCommunicationException extends CommunicationException {
    
    /**
     * 
     */
    public JMXCommunicationException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public JMXCommunicationException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public JMXCommunicationException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public JMXCommunicationException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public JMXCommunicationException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public JMXCommunicationException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
