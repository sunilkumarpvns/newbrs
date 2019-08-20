/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   AddNewServerConfigOpFailedException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.servermgr.exception                                      
 * Created on Jan 22, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.datamanager.servermgr.exception;

import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.OperationFailedException;


public class AddNewServerConfigOpFailedException extends OperationFailedException {
    
    private static final long serialVersionUID = -6223608488590766854L;

    public AddNewServerConfigOpFailedException() {
    // TODO Auto-generated constructor stub
    }
    
    public AddNewServerConfigOpFailedException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewServerConfigOpFailedException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewServerConfigOpFailedException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewServerConfigOpFailedException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    public AddNewServerConfigOpFailedException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
