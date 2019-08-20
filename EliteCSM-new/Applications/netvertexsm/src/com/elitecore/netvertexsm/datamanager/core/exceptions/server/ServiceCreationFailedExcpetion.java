/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ServiceCreationFailedExcpetion.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;


public class ServiceCreationFailedExcpetion extends BaseServerException {
    
    private static final long serialVersionUID = 5245512206175803439L;

    public ServiceCreationFailedExcpetion() {}
    
    public ServiceCreationFailedExcpetion( String message ) {
        super(message);
        
    }
    
    public ServiceCreationFailedExcpetion( Throwable cause ) {
        super(cause);
        
    }
    
    public ServiceCreationFailedExcpetion( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public ServiceCreationFailedExcpetion( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public ServiceCreationFailedExcpetion( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public ServiceCreationFailedExcpetion( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public ServiceCreationFailedExcpetion( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public ServiceCreationFailedExcpetion( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public ServiceCreationFailedExcpetion( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public ServiceCreationFailedExcpetion( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public ServiceCreationFailedExcpetion( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
