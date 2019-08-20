/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceException.java                            
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
public class DatasourceException extends BaseDatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceException() {
		 
	}

	/**
	 * @param message
	 */
	public DatasourceException(String message) {
		super(message);
		 
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceException(String message, String sourceField) {
		super(message, sourceField);
		 
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceException(String message, Throwable cause) {
		super(message, cause);
		 
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		 
	}

	/**
	 * @param cause
	 */
	public DatasourceException(Throwable cause) {
		super(cause);
		 
	}

}
