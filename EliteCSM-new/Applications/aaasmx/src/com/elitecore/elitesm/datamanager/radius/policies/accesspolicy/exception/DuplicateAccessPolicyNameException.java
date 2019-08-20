/**
 * 
 */
package com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.exception;

import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;

/**
 * @author rakeshkachhadiya
 *
 */
public class DuplicateAccessPolicyNameException extends ConstraintViolationException{

	  /**
     * 
     */
    public DuplicateAccessPolicyNameException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public DuplicateAccessPolicyNameException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DuplicateAccessPolicyNameException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public DuplicateAccessPolicyNameException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public DuplicateAccessPolicyNameException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DuplicateAccessPolicyNameException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
	
}
