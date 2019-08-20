/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InvalidFileExtensionException.java                             
 * ModualName                                     
 * Created on Oct 10, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation;



/**
 * @author kaushikvira
 *
 */
public class InvalidFileExtensionException extends DataValidationException {
    
   
    public InvalidFileExtensionException() {
   
    }
    
    /**
     * @param message
     */
    public InvalidFileExtensionException( String message ) {
        super(message);
      
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public InvalidFileExtensionException( String message ,
                                          String sourceField ) {
        super(message, sourceField);
    }
    
    /**
     * @param message
     * @param cause
     */
    public InvalidFileExtensionException( String message ,
                                          Throwable cause ) {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public InvalidFileExtensionException( Throwable cause ) {
        super(cause);
    }
    public InvalidFileExtensionException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
    
}
