/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceNonCategorizedException.java                            
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
public class DatasourceUncategorizedException extends BaseDatasourceException {
    
    /**
     * 
     */
    private static final long serialVersionUID = 7348407583265411306L;

    /**
     * 
     */
    public DatasourceUncategorizedException() {
     
    }
    
    /**
     * @param message
     */
    public DatasourceUncategorizedException( String message ) {
        super(message);
         
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DatasourceUncategorizedException( String message , String sourceField ) {
        super(message, sourceField);
         
    }
    
    /**
     * @param message
     * @param cause
     */
    public DatasourceUncategorizedException( String message , Throwable cause ) {
        super(message, cause);
         
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DatasourceUncategorizedException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
         
    }
    
    /**
     * @param cause
     */
    public DatasourceUncategorizedException( Throwable cause ) {
        super(cause);
         
    }
    
}
