/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   AddNewServiceConfigOpFailedException.java                            
 * ModualName com.elitecore.elitesm.datamanager.servermgr.exception                                      
 * Created on Jan 22, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.datamanager.servermgr.exception;

import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;


public class AddNewServiceConfigOpFailedException extends OperationFailedException {
    
    private static final long serialVersionUID = -4684078037600344482L;

    public AddNewServiceConfigOpFailedException() {
    // TODO Auto-generated constructor stub
    }
    
    public AddNewServiceConfigOpFailedException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewServiceConfigOpFailedException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewServiceConfigOpFailedException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewServiceConfigOpFailedException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewServiceConfigOpFailedException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
