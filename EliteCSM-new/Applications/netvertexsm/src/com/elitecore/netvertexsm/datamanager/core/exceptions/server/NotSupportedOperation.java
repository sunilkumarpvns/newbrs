/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   NotSupportedOperation.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;


public class NotSupportedOperation extends BaseServerException {
    
    private static final long serialVersionUID = 4838659162438580495L;

    public NotSupportedOperation() {}
    
    public NotSupportedOperation( String message ) {
        super(message);
        
    }
    
    public NotSupportedOperation( Throwable cause ) {
        super(cause);
        
    }
    
    public NotSupportedOperation( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public NotSupportedOperation( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public NotSupportedOperation( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public NotSupportedOperation( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public NotSupportedOperation( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public NotSupportedOperation( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public NotSupportedOperation( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public NotSupportedOperation( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public NotSupportedOperation( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
