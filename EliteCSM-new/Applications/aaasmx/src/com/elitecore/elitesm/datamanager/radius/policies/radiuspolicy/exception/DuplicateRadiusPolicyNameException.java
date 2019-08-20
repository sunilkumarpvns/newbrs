/**
 * 
 */
package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception;

import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;

/**
 * @author rakeshkachhadiya
 *
 */
public class DuplicateRadiusPolicyNameException extends ConstraintViolationException{

	  /**
     * 
     */
    public DuplicateRadiusPolicyNameException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public DuplicateRadiusPolicyNameException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DuplicateRadiusPolicyNameException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public DuplicateRadiusPolicyNameException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public DuplicateRadiusPolicyNameException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DuplicateRadiusPolicyNameException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
	
}
