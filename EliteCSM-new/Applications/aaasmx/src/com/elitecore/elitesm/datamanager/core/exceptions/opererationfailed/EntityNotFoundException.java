/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   EntityNotFoundException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed;


/**
 * @author himanshudobaria
 *
 */
public class EntityNotFoundException extends OperationFailedException {
    
    /**
     * 
     */
    public EntityNotFoundException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public EntityNotFoundException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public EntityNotFoundException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public EntityNotFoundException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public EntityNotFoundException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public EntityNotFoundException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
