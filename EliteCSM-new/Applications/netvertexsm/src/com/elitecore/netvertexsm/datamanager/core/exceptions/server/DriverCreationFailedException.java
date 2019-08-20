/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DriverCreationFailedException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;


public class DriverCreationFailedException extends BaseServerException {
    
    private static final long serialVersionUID = -7307418898370168251L;

    public DriverCreationFailedException() {}
    
    public DriverCreationFailedException( String message ) {
        super(message);
        
    }
    
    public DriverCreationFailedException( Throwable cause ) {
        super(cause);
        
    }
    
    public DriverCreationFailedException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public DriverCreationFailedException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public DriverCreationFailedException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public DriverCreationFailedException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public DriverCreationFailedException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public DriverCreationFailedException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public DriverCreationFailedException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public DriverCreationFailedException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public DriverCreationFailedException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
