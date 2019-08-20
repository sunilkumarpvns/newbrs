/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceLockException.java                            
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
public class DatasourceLockException extends DatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceLockException() {
		
	}

	/**
	 * @param message
	 */
	public DatasourceLockException(String message) {
		super(message);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceLockException(String message, String sourceField) {
		super(message, sourceField);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceLockException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceLockException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		
	}

	/**
	 * @param cause
	 */
	public DatasourceLockException(Throwable cause) {
		super(cause);
		
	}

}
