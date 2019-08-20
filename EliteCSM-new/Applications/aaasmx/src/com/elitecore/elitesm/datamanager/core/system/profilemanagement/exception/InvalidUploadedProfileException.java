package com.elitecore.elitesm.datamanager.core.system.profilemanagement.exception;

import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;

public class InvalidUploadedProfileException extends DataValidationException {
    public InvalidUploadedProfileException() {
        super("Profile being uploaded do not match with the currently selected Profile");
    }
   
    public InvalidUploadedProfileException(String message) {
        super(message);    
    }

    public InvalidUploadedProfileException(String message, String sourceField) {
        super(message,sourceField);    
    }

    public InvalidUploadedProfileException(String message, Throwable cause) {
        super(message, cause);    
    }
   
    public InvalidUploadedProfileException(Throwable cause) {
        super(cause);    
    }
    
    public InvalidUploadedProfileException(String message,String sourceField,Throwable cause){
        super(message, sourceField, cause);
    }

}
