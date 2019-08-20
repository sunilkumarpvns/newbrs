/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   BaseDBException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.db                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.datasource;

import com.elitecore.netvertexsm.datamanager.DataManagerException;

/**
 * @author himanshudobaria
 * 
 */
public class BaseDatasourceException extends DataManagerException {

	private static final long serialVersionUID = -7854913682584160405L;

	public BaseDatasourceException() {
	}

	/**
	 * @param message
	 */
	public BaseDatasourceException(String message) {
		super(message);

	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public BaseDatasourceException(String message, String sourceField) {
		super(message, sourceField);

	}

	/**
	 * @param message
	 * @param cause
	 */
	public BaseDatasourceException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public BaseDatasourceException(String message, String sourceField, Throwable cause) {
		super(message, sourceField, cause);

	}

	/**
	 * @param cause
	 */
	public BaseDatasourceException(Throwable cause) {
		super(cause);

	}

}
