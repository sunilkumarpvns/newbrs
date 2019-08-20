/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DatasourceSQLGrammarException.java                            
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
public class DatasourceSQLGrammarException extends DatasourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceSQLGrammarException() {
		 
	}

	/**
	 * @param message
	 */
	public DatasourceSQLGrammarException(String message) {
		super(message);
		 
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public DatasourceSQLGrammarException(String message, String sourceField) {
		super(message, sourceField);
		 
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceSQLGrammarException(String message, Throwable cause) {
		super(message, cause);
		 
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public DatasourceSQLGrammarException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		 
	}

	/**
	 * @param cause
	 */
	public DatasourceSQLGrammarException(Throwable cause) {
		super(cause);
		 
	}

}
