package com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception;

import com.elitecore.elitesm.datamanager.DataManagerException;

public class DatabaseConnectionException extends DataManagerException {
	
	public DatabaseConnectionException() {
		// TODO Auto-generated constructor stub
	}

	public DatabaseConnectionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public DatabaseConnectionException(String message, String sourceField) {
		super(message, sourceField);
		// TODO Auto-generated constructor stub
	}

	public DatabaseConnectionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public DatabaseConnectionException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		// TODO Auto-generated constructor stub
	}

	public DatabaseConnectionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
