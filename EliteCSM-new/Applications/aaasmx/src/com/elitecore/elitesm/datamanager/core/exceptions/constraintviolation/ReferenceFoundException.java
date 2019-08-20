package com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation;

import com.elitecore.elitesm.datamanager.DataManagerException;

public class ReferenceFoundException extends DataManagerException{

	
	 /**
     * 
     */
    public ReferenceFoundException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public ReferenceFoundException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public ReferenceFoundException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public ReferenceFoundException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public ReferenceFoundException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public ReferenceFoundException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
