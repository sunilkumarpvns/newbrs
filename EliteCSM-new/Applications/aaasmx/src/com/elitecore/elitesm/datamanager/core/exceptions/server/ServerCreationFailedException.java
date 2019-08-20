/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ServerCreationFailedException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.datamanager.core.exceptions.server;

import java.util.List;


public class ServerCreationFailedException extends BaseServerException {
    
    private static final long serialVersionUID = -642084890959767776L;

    public ServerCreationFailedException() {}
    
    public ServerCreationFailedException( String message ) {
        super(message);
        
    }
    
    public ServerCreationFailedException( Throwable cause ) {
        super(cause);
        
    }
    
    public ServerCreationFailedException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public ServerCreationFailedException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public ServerCreationFailedException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public ServerCreationFailedException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public ServerCreationFailedException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public ServerCreationFailedException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public ServerCreationFailedException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public ServerCreationFailedException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public ServerCreationFailedException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
