package com.elitecore.netvertexsm.datamanager.core.exceptions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;


public class DataValidationException extends DataManagerException{
    

    public DataValidationException() {
        super("Error in Update Data Manager.");
    }
   
    public DataValidationException(String message) {
        super(message);    
    }

    public DataValidationException(String message, String sourceField) {
        super(message,sourceField);    
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);    
    }
   
    public DataValidationException(Throwable cause) {
        super(cause);    
    }
    
    public DataValidationException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
}
