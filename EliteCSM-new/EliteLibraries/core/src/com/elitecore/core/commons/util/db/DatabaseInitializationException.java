/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 10th April 2006
 *  Author : Ezhava Baiju D  
 */

package com.elitecore.core.commons.util.db;

import java.sql.SQLException;

/**
 * Thrown to indicate a technical problem.  
 *
 */
public class DatabaseInitializationException extends Exception {

	private static final long serialVersionUID = 1L;
	private int sqlErrorCode = 0;


	public DatabaseInitializationException(String message) {
		super(message);
	}

	public DatabaseInitializationException(Throwable cause) {
		super(cause);
	}

	public DatabaseInitializationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DatabaseInitializationException(SQLException sqlException) {
		super(sqlException);
		this.sqlErrorCode = sqlException.getErrorCode();
	}
	
	public DatabaseInitializationException(String message, SQLException sqlException) {
		super(message, sqlException);
		this.sqlErrorCode = sqlException.getErrorCode();
	}
	
	public int getSqlErrorCode() {
		return this.sqlErrorCode;
	}
}