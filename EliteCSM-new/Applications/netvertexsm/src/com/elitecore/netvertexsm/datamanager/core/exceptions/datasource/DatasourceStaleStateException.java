/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceTypeMismatchException.java                            
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
public class DatasourceStaleStateException extends BaseDatasourceException {

	/**
	 * 
	 */
	public DatasourceStaleStateException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DatasourceStaleStateException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceStaleStateException(String message, String sourceField) {
		super(message, sourceField);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceStaleStateException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceStaleStateException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DatasourceStaleStateException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
