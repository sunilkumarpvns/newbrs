/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InvalidHttpSessionException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.datavalidation                                      
 * Created on Apr 18, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.datamanager.core.exceptions.datavalidation;

public class InvalidHttpSessionException extends DataValidationException {

    public InvalidHttpSessionException() {
        // TODO Auto-generated constructor stub
    }

    public InvalidHttpSessionException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public InvalidHttpSessionException(String message, String sourceField) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }

    public InvalidHttpSessionException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public InvalidHttpSessionException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public InvalidHttpSessionException(String message, String sourceField, Throwable cause) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }

}
