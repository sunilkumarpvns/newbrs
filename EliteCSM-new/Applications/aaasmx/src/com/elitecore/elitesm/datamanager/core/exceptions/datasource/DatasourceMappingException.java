/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceMappingException.java                            
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
public class DatasourceMappingException extends BaseDatasourceException {
    
    /**
     * 
     */
    public DatasourceMappingException() {
     
    }
    
    /**
     * @param message
     */
    public DatasourceMappingException( String message ) {
        super(message);
         
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DatasourceMappingException( String message , String sourceField ) {
        super(message, sourceField);
         
    }
    
    /**
     * @param message
     * @param cause
     */
    public DatasourceMappingException( String message , Throwable cause ) {
        super(message, cause);
         
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DatasourceMappingException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
         
    }
    
    /**
     * @param cause
     */
    public DatasourceMappingException( Throwable cause ) {
        super(cause);
         
    }
    
}
