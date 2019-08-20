/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitializationFailedException.java                             
 * ModualName                                     
 * Created on Oct 19, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed;


/**
 * @author kaushikvira
 *
 */
public class InitializationFailedException extends OperationFailedException {
    
    /**
     * 
     */
    public InitializationFailedException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public InitializationFailedException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public InitializationFailedException( String message ,
                                          String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public InitializationFailedException( String message ,
                                          Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public InitializationFailedException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public InitializationFailedException( String message ,
                                          String sourceField ,
                                          Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
