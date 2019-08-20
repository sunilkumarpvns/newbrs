package com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception;

import com.elitecore.elitesm.datamanager.DataManagerException;

public class InvalidSQLStatementException extends DataManagerException {

	public InvalidSQLStatementException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidSQLStatementException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidSQLStatementException(String message, String sourceField) {
		super(message, sourceField);
		// TODO Auto-generated constructor stub
	}

	public InvalidSQLStatementException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidSQLStatementException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidSQLStatementException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
