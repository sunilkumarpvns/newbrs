/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InvalidValueException.java                             
 * ModualName                                     
 * Created on Oct 10, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.elitesm.datamanager.core.exceptions.datavalidation;

import com.elitecore.elitesm.datamanager.DataManagerException;



/**
 * @author Soniya.Patel
 *
 */
public class InputParameterMissingException extends DataManagerException {
    
    /**
     * 
     */
    public InputParameterMissingException() {
   
    }
    
    /**
     * @param message
     */
    public InputParameterMissingException( String message ) {
        super(message);
       
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public InputParameterMissingException( String message ,
                                  String sourceField ) {
        super(message, sourceField);
     
    }
    
    /**
     * @param message
     * @param cause
     */
    public InputParameterMissingException( String message ,
                                  Throwable cause ) {
        super(message, cause);
      
    }
    
    /**
     * @param cause
     */
    public InputParameterMissingException( Throwable cause ) {
        super(cause);
     
    }
    public InputParameterMissingException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
    
}
