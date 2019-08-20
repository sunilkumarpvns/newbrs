/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UniqueResultExpectedException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed                                      
 * Created on Mar 10, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed;


public class UniqueResultExpectedException extends OperationFailedException {
    
    private static final long serialVersionUID = -7564317969273167392L;

    public UniqueResultExpectedException() {}
    
    public UniqueResultExpectedException( String message ) {
        super(message);
        
    }
    
    public UniqueResultExpectedException( String message , String sourceField ) {
        super(message, sourceField);
        
    }
    
    public UniqueResultExpectedException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public UniqueResultExpectedException( Throwable cause ) {
        super(cause);
        
    }
    
    public UniqueResultExpectedException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        
    }
    
}
