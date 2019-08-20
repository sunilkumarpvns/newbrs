/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceDuplicateMappingException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.datasource                                    
 * Created on Feb 1, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.elitesm.datamanager.core.exceptions.datasource;


/**
 * @author himanshudobaria
 *
 */
public class DatasourceDuplicateMappingException extends DatasourceMappingException {
    
    /**
     * 
     */
    public DatasourceDuplicateMappingException() {
    
    }
    
    /**
     * @param message
     */
    public DatasourceDuplicateMappingException( String message ) {
        super(message);
        
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DatasourceDuplicateMappingException( String message , String sourceField ) {
        super(message, sourceField);
        
    }
    
    /**
     * @param message
     * @param cause
     */
    public DatasourceDuplicateMappingException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DatasourceDuplicateMappingException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        
    }
    
    /**
     * @param cause
     */
    public DatasourceDuplicateMappingException( Throwable cause ) {
        super(cause);
        
    }
    
}
