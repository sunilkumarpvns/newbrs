/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   OperationFailedException.java                             
 * ModualName                                     
 * Created on Oct 8, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed;

import com.elitecore.netvertexsm.datamanager.DataManagerException;

/**
 * @author kaushikvira
 */
public class OperationFailedException extends DataManagerException {
    
    /**
     * 
     */
    public OperationFailedException() {

    }
    
    /**
     * @param message
     */
    public OperationFailedException( String message ) {
        super(message);
        
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public OperationFailedException( String message ,
                                    String sourceField ) {
        super(message, sourceField);
        
    }
    
    /**
     * @param message
     * @param cause
     */
    public OperationFailedException( String message ,
                                    Throwable cause ) {
        super(message, cause);
        
    }
    
    /**
     * @param cause
     */
    public OperationFailedException( Throwable cause ) {
        super(cause);
        
    }
    public OperationFailedException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
}
