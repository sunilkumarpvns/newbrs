/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DriverNotFoundException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;


public class DriverNotFoundException extends BaseServerException {
    
    private static final long serialVersionUID = -849525697459564124L;

    public DriverNotFoundException() {}
    
    public DriverNotFoundException( String message ) {
        super(message);
        
    }
    
    public DriverNotFoundException( Throwable cause ) {
        super(cause);
        
    }
    
    public DriverNotFoundException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public DriverNotFoundException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public DriverNotFoundException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public DriverNotFoundException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public DriverNotFoundException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public DriverNotFoundException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public DriverNotFoundException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public DriverNotFoundException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public DriverNotFoundException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
