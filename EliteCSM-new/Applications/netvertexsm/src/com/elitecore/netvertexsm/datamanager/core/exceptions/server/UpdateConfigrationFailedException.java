/**
 * Copyright (C) Elitecore Technologies Ltd. FileName
 * UpdateConfigrationFailedException.java ModualName
 * com.elitecore.netvertexsm.datamanager.core.exceptions.server Created on Feb 14,
 * 2008 Last Modified on
 * 
 * @author : kaushikvira
 */

package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;

public class UpdateConfigrationFailedException extends BaseServerException {
    
    private static final long serialVersionUID = 5453280925770441488L;
    
    public UpdateConfigrationFailedException() {}
    
    public UpdateConfigrationFailedException( String message ) {
        super(message);
        
    }
    
    public UpdateConfigrationFailedException( Throwable cause ) {
        super(cause);
        
    }
    
    public UpdateConfigrationFailedException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public UpdateConfigrationFailedException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public UpdateConfigrationFailedException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public UpdateConfigrationFailedException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public UpdateConfigrationFailedException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public UpdateConfigrationFailedException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public UpdateConfigrationFailedException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public UpdateConfigrationFailedException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public UpdateConfigrationFailedException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
