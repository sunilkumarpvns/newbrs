/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceTransactionException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.db                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.datasource;

/**
 * @author himanshudobaria
 *
 */
public class DatasourceTransactionException extends BaseDatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceTransactionException() {
		
	}

	/**
	 * @param message
	 */
	public DatasourceTransactionException(String message) {
		super(message);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceTransactionException(String message, String sourceField) {
		super(message, sourceField);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceTransactionException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceTransactionException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		
	}

	/**
	 * @param cause
	 */
	public DatasourceTransactionException(Throwable cause) {
		super(cause);
		
	}

}
