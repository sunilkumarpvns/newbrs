/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ServiceNotFoundException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.datamanager.core.exceptions.server;

import java.util.List;


public class ServiceNotFoundException extends BaseServerException {
    
    private static final long serialVersionUID = 3252646844704703799L;

    public ServiceNotFoundException() {}
    
    public ServiceNotFoundException( String message ) {
        super(message);
        
    }
    
    public ServiceNotFoundException( Throwable cause ) {
        super(cause);
        
    }
    
    public ServiceNotFoundException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public ServiceNotFoundException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public ServiceNotFoundException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public ServiceNotFoundException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public ServiceNotFoundException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public ServiceNotFoundException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public ServiceNotFoundException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public ServiceNotFoundException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public ServiceNotFoundException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
