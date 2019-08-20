/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceIdentifierGenerationException.java                            
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
public class DatasourceIdentifierGenerationException extends
		BaseDatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceIdentifierGenerationException() {
		
	}

	/**
	 * @param message
	 */
	public DatasourceIdentifierGenerationException(String message) {
		super(message);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceIdentifierGenerationException(String message,
			String sourceField) {
		super(message, sourceField);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceIdentifierGenerationException(String message,
			Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceIdentifierGenerationException(String message,
			String sourceField, Throwable cause) {
		super(message, sourceField, cause);
		
	}

	/**
	 * @param cause
	 */
	public DatasourceIdentifierGenerationException(Throwable cause) {
		super(cause);
		
	}

}
