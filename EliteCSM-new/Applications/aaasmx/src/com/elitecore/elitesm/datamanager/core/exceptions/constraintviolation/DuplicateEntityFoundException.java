/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DuplicateEntityFoundException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation;


/**
 * @author himanshudobaria
 *
 */
public class DuplicateEntityFoundException extends ConstraintViolationException {
    
    /**
     * 
     */
    public DuplicateEntityFoundException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public DuplicateEntityFoundException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DuplicateEntityFoundException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public DuplicateEntityFoundException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public DuplicateEntityFoundException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DuplicateEntityFoundException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
