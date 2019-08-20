/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DuplicateParameterFoundExcpetion.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server                                      
 * Created on Feb 14, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.util.List;


public class DuplicateParameterFoundExcpetion extends BaseServerException {
    
    private static final long serialVersionUID = -3349540325579048769L;

    public DuplicateParameterFoundExcpetion() {}
    
    public DuplicateParameterFoundExcpetion( String message ) {
        super(message);
        
    }
    
    public DuplicateParameterFoundExcpetion( Throwable cause ) {
        super(cause);
        
    }
    
    public DuplicateParameterFoundExcpetion( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    public DuplicateParameterFoundExcpetion( String message , String param1 ) {
        super(message, param1);
        
    }
    
    public DuplicateParameterFoundExcpetion( String message , String param1 , String param2 ) {
        super(message, param1, param2);
        
    }
    
    public DuplicateParameterFoundExcpetion( String message , String param1 , String param2 , String param3 ) {
        super(message, param1, param2, param3);
        
    }
    
    public DuplicateParameterFoundExcpetion( String message , String param1 , Throwable cause ) {
        super(message, param1, cause);
        
    }
    
    public DuplicateParameterFoundExcpetion( String message , String param1 , String param2 , Throwable cause ) {
        super(message, param1, param2, cause);
        
    }
    
    public DuplicateParameterFoundExcpetion( String message , List<String> listOfParam ) {
        super(message, listOfParam);
        
    }
    
    public DuplicateParameterFoundExcpetion( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, param1, param2, param3, cause);
        
    }
    
    public DuplicateParameterFoundExcpetion( String message , List<String> listOfParam , Throwable cause ) {
        super(message, listOfParam, cause);
        
    }
    
}
