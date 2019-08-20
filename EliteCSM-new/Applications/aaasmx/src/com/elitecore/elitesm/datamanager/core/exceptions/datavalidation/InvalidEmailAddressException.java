/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InvalidEmailAddressException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.datavalidation                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.elitesm.datamanager.core.exceptions.datavalidation;


/**
 * @author himanshudobaria
 *
 */
public class InvalidEmailAddressException extends DataValidationException {
    
    /**
     * 
     */
    public InvalidEmailAddressException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public InvalidEmailAddressException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public InvalidEmailAddressException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public InvalidEmailAddressException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public InvalidEmailAddressException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public InvalidEmailAddressException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
