/**
 * 
 */
package com.elitecore.elitesm.datamanager.radius.dictionary.exception;

import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;

/**
 * @author rakeshkachhadiya
 *
 */
public class UpdateDictionaryException extends ConstraintViolationException{

	  /**
     * 
     */
    public UpdateDictionaryException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public UpdateDictionaryException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public UpdateDictionaryException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public UpdateDictionaryException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public UpdateDictionaryException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public UpdateDictionaryException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
	
}
