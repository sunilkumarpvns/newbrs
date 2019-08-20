/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SynchronizationFailedException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;


public class SynchronizationFailedException extends BaseServerException {
    
    private static final long serialVersionUID = 1L;

    public SynchronizationFailedException() {}
    
    public SynchronizationFailedException( String message ) {
        super(message);
        
    }
    
    public SynchronizationFailedException( Throwable cause ) {
        super(cause);
        
    }
    
    public SynchronizationFailedException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public SynchronizationFailedException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public SynchronizationFailedException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public SynchronizationFailedException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public SynchronizationFailedException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public SynchronizationFailedException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public SynchronizationFailedException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public SynchronizationFailedException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public SynchronizationFailedException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
