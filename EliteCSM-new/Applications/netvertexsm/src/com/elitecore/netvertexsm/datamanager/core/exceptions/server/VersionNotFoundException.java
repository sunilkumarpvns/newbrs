/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   VersionNotFoundException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server                                      
 * Created on Mar 6, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;


public class VersionNotFoundException extends BaseServerException {
    
    private static final long serialVersionUID = -1637641132747088266L;

    public VersionNotFoundException() {}
    
    public VersionNotFoundException( String message ) {
        super(message);
        
    }
    
    public VersionNotFoundException( Throwable cause ) {
        super(cause);
        
    }
    
    public VersionNotFoundException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public VersionNotFoundException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public VersionNotFoundException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public VersionNotFoundException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public VersionNotFoundException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public VersionNotFoundException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public VersionNotFoundException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public VersionNotFoundException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public VersionNotFoundException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
