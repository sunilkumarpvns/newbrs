/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceConstraintViolationException.java                            
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
public class DatasourceConstraintViolationException extends
DatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceConstraintViolationException() {
		
	}

	/**
	 * @param message
	 */
	public DatasourceConstraintViolationException(String message) {
		super(message);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceConstraintViolationException(String message,
			String sourceField) {
		super(message, sourceField);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceConstraintViolationException(String message,
			Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceConstraintViolationException(String message,
			String sourceField, Throwable cause) {
		super(message, sourceField, cause);
		
	}

	/**
	 * @param cause
	 */
	public DatasourceConstraintViolationException(Throwable cause) {
		super(cause);
		
	}

}
