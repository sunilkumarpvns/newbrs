/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourcePropertyNotFoundException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.datasource                                    
 * Created on Feb 1, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.datasource;


/**
 * @author himanshudobaria
 *
 */
public class DatasourcePropertyNotFoundException extends DatasourceMappingException {
    
    /**
     * 
     */
    public DatasourcePropertyNotFoundException() {
    
    }
    
    /**
     * @param message
     */
    public DatasourcePropertyNotFoundException( String message ) {
        super(message);
        
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DatasourcePropertyNotFoundException( String message , String sourceField ) {
        super(message, sourceField);
        
    }
    
    /**
     * @param message
     * @param cause
     */
    public DatasourcePropertyNotFoundException( String message , Throwable cause ) {
        super(message, cause);
        
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DatasourcePropertyNotFoundException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        
    }
    
    /**
     * @param cause
     */
    public DatasourcePropertyNotFoundException( Throwable cause ) {
        super(cause);
        
    }
    
}
