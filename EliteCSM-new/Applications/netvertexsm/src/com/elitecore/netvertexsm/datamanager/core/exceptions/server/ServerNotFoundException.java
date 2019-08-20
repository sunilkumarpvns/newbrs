/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ServerNotFoundException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;


public class ServerNotFoundException extends BaseServerException {
    
    private static final long serialVersionUID = -7300363955621596586L;

    public ServerNotFoundException() {}
    
    public ServerNotFoundException( String message ) {
        super(message);
        
    }
    
    public ServerNotFoundException( Throwable cause ) {
        super(cause);
        
    }
    
    public ServerNotFoundException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public ServerNotFoundException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public ServerNotFoundException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public ServerNotFoundException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public ServerNotFoundException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public ServerNotFoundException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public ServerNotFoundException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public ServerNotFoundException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public ServerNotFoundException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
