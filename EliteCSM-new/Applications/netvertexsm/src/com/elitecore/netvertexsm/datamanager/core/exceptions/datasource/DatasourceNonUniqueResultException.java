/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceNonUniqueResultException.java                            
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
public class DatasourceNonUniqueResultException extends BaseDatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceNonUniqueResultException() {
		
	}

	/**
	 * @param message
	 */
	public DatasourceNonUniqueResultException(String message) {
		super(message);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceNonUniqueResultException(String message, String sourceField) {
		super(message, sourceField);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceNonUniqueResultException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceNonUniqueResultException(String message,
			String sourceField, Throwable cause) {
		super(message, sourceField, cause);
		
	}

	/**
	 * @param cause
	 */
	public DatasourceNonUniqueResultException(Throwable cause) {
		super(cause);
		
	}

}
