/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ParameterNotFoundException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;


public class ParameterNotFoundException extends BaseServerException {
    
    private static final long serialVersionUID = -1782994534592635795L;

    public ParameterNotFoundException() {}
    
    public ParameterNotFoundException( String message ) {
        super(message);
        
    }
    
    public ParameterNotFoundException( Throwable cause ) {
        super(cause);
        
    }
    
    public ParameterNotFoundException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public ParameterNotFoundException( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public ParameterNotFoundException( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public ParameterNotFoundException( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public ParameterNotFoundException( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public ParameterNotFoundException( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public ParameterNotFoundException( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public ParameterNotFoundException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public ParameterNotFoundException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
