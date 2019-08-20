/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   JDBCConnectionException.java                            
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
public class DatasourceConnectionException extends DatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceConnectionException() {
		 
	}

	/**
	 * @param message
	 */
	public DatasourceConnectionException(String message) {
		super(message);
		 
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceConnectionException(String message, String sourceField) {
		super(message, sourceField);
		 
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceConnectionException(String message, Throwable cause) {
		super(message, cause);
		 
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceConnectionException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		 
	}

	/**
	 * @param cause
	 */
	public DatasourceConnectionException(Throwable cause) {
		super(cause);
		 
	}

}
