/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SchemaMissMatchException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.datavalidation                                      
 * Created on Jan 18, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.datamanager.core.exceptions.datavalidation;


public class SchemaMissMatchException extends DataValidationException {
    
    private static final long serialVersionUID = -4874366065597989691L;

    public SchemaMissMatchException() {
    }
    
    public SchemaMissMatchException( String message ) {
        super(message);
    }
    
    public SchemaMissMatchException( String message , String sourceField ) {
        super(message, sourceField);
    }
    
    public SchemaMissMatchException( String message , Throwable cause ) {
        super(message, cause);
    }
    
    public SchemaMissMatchException( Throwable cause ) {
        super(cause);
      
    }
    
    public SchemaMissMatchException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);

    }
    
}
