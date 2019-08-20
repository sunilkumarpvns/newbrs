/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InvalidContentTypeException.java                             
 * ModualName                                     
 * Created on Dec 29, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.datamanager.core.exceptions.datavalidation;

public class InvalidClassCastException extends DataValidationException {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InvalidClassCastException() {}
    
    public InvalidClassCastException( String message ) {
        super(message);
    }
    
    public InvalidClassCastException( String message , String sourceField ) {
        super(message, sourceField);
    }
    
    public InvalidClassCastException( String message , Throwable cause ) {
        super(message, cause);
    }
    
    public InvalidClassCastException( Throwable cause ) {
        super(cause);
    }
    
    public InvalidClassCastException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
    }
    
}
