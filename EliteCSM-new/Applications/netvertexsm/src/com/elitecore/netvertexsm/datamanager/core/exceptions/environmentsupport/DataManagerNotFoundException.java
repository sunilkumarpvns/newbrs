/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DataManagerNotFoundException.java                            
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.environmentsupport                                    
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.netvertexsm.datamanager.core.exceptions.environmentsupport;


/**
 * @author himanshudobaria
 *
 */
public class DataManagerNotFoundException extends EnvironmentNotSupportedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DataManagerNotFoundException() {
	}

	/**
	 * @param message
	 */
	public DataManagerNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DataManagerNotFoundException(String message, String sourceField) {
		super(message, sourceField);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataManagerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DataManagerNotFoundException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
	}

	/**
	 * @param cause
	 */
	public DataManagerNotFoundException(Throwable cause) {
		super(cause);
	}

}
