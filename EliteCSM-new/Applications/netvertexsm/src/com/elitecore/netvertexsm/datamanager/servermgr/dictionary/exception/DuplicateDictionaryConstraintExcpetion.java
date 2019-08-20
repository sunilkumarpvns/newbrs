/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DuplicateDictionaryConstraintExcpetion.java                             
 * ModualName                                     
 * Created on Oct 23, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.exception;

import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;


/**
 * @author kaushikvira
 *
 */
public class DuplicateDictionaryConstraintExcpetion extends ConstraintViolationException {
    
    /**
     * 
     */
    public DuplicateDictionaryConstraintExcpetion() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public DuplicateDictionaryConstraintExcpetion( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DuplicateDictionaryConstraintExcpetion( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public DuplicateDictionaryConstraintExcpetion( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public DuplicateDictionaryConstraintExcpetion( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DuplicateDictionaryConstraintExcpetion( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
