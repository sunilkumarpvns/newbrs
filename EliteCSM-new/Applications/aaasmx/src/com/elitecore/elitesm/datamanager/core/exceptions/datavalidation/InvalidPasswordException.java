package com.elitecore.elitesm.datamanager.core.exceptions.datavalidation;

public class InvalidPasswordException extends DataValidationException {
	/**
     * 
     */
    public InvalidPasswordException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public InvalidPasswordException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public InvalidPasswordException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public InvalidPasswordException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public InvalidPasswordException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public InvalidPasswordException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
