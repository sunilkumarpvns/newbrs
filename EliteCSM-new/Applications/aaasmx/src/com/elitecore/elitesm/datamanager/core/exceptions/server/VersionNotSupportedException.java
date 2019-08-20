/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   VersionNotSupportedException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.datamanager.core.exceptions.server;

import java.util.List;


public class VersionNotSupportedException extends BaseServerException {
    
    private static final long serialVersionUID = 8482722300924857042L;

    public VersionNotSupportedException() {}
    
    public VersionNotSupportedException( String message ) {
        super(message);
        
    }
    
    public VersionNotSupportedException( Throwable cause ) {
        super(cause);
        
    }
    
    public VersionNotSupportedException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public VersionNotSupportedException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public VersionNotSupportedException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public VersionNotSupportedException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public VersionNotSupportedException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public VersionNotSupportedException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public VersionNotSupportedException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public VersionNotSupportedException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public VersionNotSupportedException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
