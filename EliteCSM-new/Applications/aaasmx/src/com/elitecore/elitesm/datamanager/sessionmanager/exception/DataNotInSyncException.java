package com.elitecore.elitesm.datamanager.sessionmanager.exception;

import com.elitecore.elitesm.datamanager.DataManagerException;

public class DataNotInSyncException extends DataManagerException {
	
	public DataNotInSyncException() {
        super("Database Not in Sync.");
    }
   
    public DataNotInSyncException(String message) {
        super(message);  
    }
 
    public DataNotInSyncException(String message,Throwable cause) {
        super(message, cause);  
    }
    
    public DataNotInSyncException(Throwable cause) {
        super(cause);  
    }
    
    
    public DataNotInSyncException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    

}
