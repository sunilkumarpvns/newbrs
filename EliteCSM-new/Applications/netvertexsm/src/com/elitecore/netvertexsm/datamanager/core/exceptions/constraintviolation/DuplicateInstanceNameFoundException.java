/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DuplicateEntityFoundException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation;


/**
 * @author mayurmistry
 *
 */
public class DuplicateInstanceNameFoundException extends ConstraintViolationException {
    
    /**
     * 
     */
    public DuplicateInstanceNameFoundException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public DuplicateInstanceNameFoundException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DuplicateInstanceNameFoundException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public DuplicateInstanceNameFoundException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public DuplicateInstanceNameFoundException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DuplicateInstanceNameFoundException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
