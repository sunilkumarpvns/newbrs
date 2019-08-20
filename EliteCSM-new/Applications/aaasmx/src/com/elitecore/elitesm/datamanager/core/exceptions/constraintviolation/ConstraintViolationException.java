package com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation;

import com.elitecore.elitesm.datamanager.DataManagerException;

public class ConstraintViolationException extends DataManagerException{
    

    public ConstraintViolationException() {
        super("Error in Update Data Manager.");
    }
   
    public ConstraintViolationException(String message) {
        super(message);    
    }

    public ConstraintViolationException(String message, String sourceField) {
        super(message,sourceField);    
    }

    public ConstraintViolationException(String message, Throwable cause) {
        super(message, cause);    
    }
   
    public ConstraintViolationException(Throwable cause) {
        super(cause);    
    }
    
    public ConstraintViolationException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }
    
}
