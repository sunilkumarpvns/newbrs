/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceGenericException.java                            
 * ModualName com.elitecore.elitesm.datamanager.core.exceptions.db                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.elitesm.datamanager.core.exceptions.datasource;

/**
 * @author himanshudobaria
 *
 */
public class DatasourceGenericException extends DatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceGenericException() {
		
	}

	/**
	 * @param message
	 */
	public DatasourceGenericException(String message) {
		super(message);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceGenericException(String message, String sourceField) {
		super(message, sourceField);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceGenericException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceGenericException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		
	}

	/**
	 * @param cause
	 */
	public DatasourceGenericException(Throwable cause) {
		super(cause);
		
	}

}
