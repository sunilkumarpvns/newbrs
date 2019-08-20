/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InvalidArrguementsException.java                             
 * ModualName                                     
 * Created on Dec 3, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation;


/**
 * @author kaushikvira
 *
 */
public class InvalidArrguementsException extends DataValidationException {
    
    /**
     * 
     */
    public InvalidArrguementsException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public InvalidArrguementsException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public InvalidArrguementsException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public InvalidArrguementsException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public InvalidArrguementsException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public InvalidArrguementsException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
