/**
 * 
 */
package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.exception;

import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;


/**
 * @author kaushikvira
 *
 */
public class DatasourceSchemaMisMatchException extends DataValidationException {
    
    /**
     * 
     */
    public DatasourceSchemaMisMatchException() {
    // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public DatasourceSchemaMisMatchException( String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public DatasourceSchemaMisMatchException( String message , String sourceField ) {
        super(message, sourceField);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param cause
     */
    public DatasourceSchemaMisMatchException( String message , Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param cause
     */
    public DatasourceSchemaMisMatchException( Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public DatasourceSchemaMisMatchException( String message , String sourceField , Throwable cause ) {
        super(message, sourceField, cause);
        // TODO Auto-generated constructor stub
    }
    
}
