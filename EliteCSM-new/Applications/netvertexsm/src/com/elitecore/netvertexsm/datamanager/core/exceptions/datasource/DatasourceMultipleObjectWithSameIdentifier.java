/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceMultipleObjectWithSameIdentifier.java                            
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
public class DatasourceMultipleObjectWithSameIdentifier extends
		BaseDatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceMultipleObjectWithSameIdentifier() {
		
	}

	/**
	 * @param message
	 */
	public DatasourceMultipleObjectWithSameIdentifier(String message) {
		super(message);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceMultipleObjectWithSameIdentifier(String message,
			String sourceField) {
		super(message, sourceField);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceMultipleObjectWithSameIdentifier(String message,
			Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceMultipleObjectWithSameIdentifier(String message,
			String sourceField, Throwable cause) {
		super(message, sourceField, cause);
		
	}

	/**
	 * @param cause
	 */
	public DatasourceMultipleObjectWithSameIdentifier(Throwable cause) {
		super(cause);
		
	}

}
