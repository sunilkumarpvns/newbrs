/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ActionNotPermitedException.java                            
 * ModualName exceptions.auhorization                                   
 * Created on Jan 31, 2008
 * Last Modified on                                     
 * @author :  himanshudobaria
 */
package com.elitecore.elitesm.datamanager.core.exceptions.auhorization;


/**
 * @author himanshudobaria
 *
 */
public class ActionNotPermitedException extends AuthorizationException {

	/**
     * 
     */
    private static final long serialVersionUID = -1171926269192552118L;

    /**
	 * 
	 */
	public ActionNotPermitedException() {
		 
	}

	/**
	 * @param message
	 */
	public ActionNotPermitedException(String message) {
		super(message);
		 
	}

	/**
	 * @param message
	 * @param sourceField
	 */
	public ActionNotPermitedException(String message, String sourceField) {
		super(message, sourceField);
		 
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ActionNotPermitedException(String message, Throwable cause) {
		super(message, cause);
		 
	}

	/**
	 * @param message
	 * @param sourceField
	 * @param cause
	 */
	public ActionNotPermitedException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		 
	}

	/**
	 * @param cause
	 */
	public ActionNotPermitedException(Throwable cause) {
		super(cause);
		 
	}

}
