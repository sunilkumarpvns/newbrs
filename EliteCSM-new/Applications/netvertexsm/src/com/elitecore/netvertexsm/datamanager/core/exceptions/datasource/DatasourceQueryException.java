/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceBatchFailedException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.db                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.datasource;


public class DatasourceQueryException extends BaseDatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceQueryException() {
		
	}

	/**
	 * @param message
	 */
	public DatasourceQueryException(String message) {
		super(message);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceQueryException(String message, String sourceField) {
		super(message, sourceField);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceQueryException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceQueryException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		
	}

	/**
	 * @param cause
	 */
	public DatasourceQueryException(Throwable cause) {
		super(cause);
		
	}

}
