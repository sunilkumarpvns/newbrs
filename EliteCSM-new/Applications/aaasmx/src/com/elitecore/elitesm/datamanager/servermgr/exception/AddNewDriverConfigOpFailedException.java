/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   AddNewDriverConfigOpFailedException.java                            
 * ModualName com.elitecore.elitesm.datamanager.servermgr.exception                                      
 * Created on Jan 22, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.datamanager.servermgr.exception;

import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;


public class AddNewDriverConfigOpFailedException extends OperationFailedException {
    
    private static final long serialVersionUID = -5259589048469904347L;

    public AddNewDriverConfigOpFailedException() {
    // TODO Auto-generated constructor stub
    }
    
    public AddNewDriverConfigOpFailedException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewDriverConfigOpFailedException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewDriverConfigOpFailedException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewDriverConfigOpFailedException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewDriverConfigOpFailedException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
