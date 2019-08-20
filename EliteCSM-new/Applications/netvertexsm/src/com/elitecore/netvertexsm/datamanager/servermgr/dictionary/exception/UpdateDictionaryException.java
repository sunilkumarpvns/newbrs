/**
 * 
 */
package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.exception;

import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;

public class UpdateDictionaryException extends ConstraintViolationException{

	private static final long serialVersionUID = 1L;

    public UpdateDictionaryException() {
    }
    
    /**
     * @param message
     */
    public UpdateDictionaryException( String message ) {
        super(message);
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public UpdateDictionaryException( String message , String sourceField ) {
        super(message, sourceField);
   }
    
    /**
     * @param message
     * @param cause
     */
    public UpdateDictionaryException( String message , Throwable cause ) {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UpdateDictionaryException( Throwable cause ) {
        super(cause);
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public UpdateDictionaryException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
    }
	
}
