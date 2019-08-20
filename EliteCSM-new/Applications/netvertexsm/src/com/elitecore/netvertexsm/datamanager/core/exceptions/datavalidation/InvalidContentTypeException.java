/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InvalidContentTypeException.java                             
 * ModualName                                     
 * Created on Dec 29, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation;

public class InvalidContentTypeException extends DataValidationException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidContentTypeException() {}
    
    public InvalidContentTypeException( String message ) {
        super(message);
    }
    
    public InvalidContentTypeException( String message , String sourceField ) {
        super(message, sourceField);
        
    }
    
    public InvalidContentTypeException( String message , Throwable cause ) {
        super(message, cause);
    }
    
    public InvalidContentTypeException( Throwable cause ) {
        super(cause);
    }
    
    public InvalidContentTypeException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
    }
    
}
