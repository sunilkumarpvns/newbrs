package com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception;

import com.elitecore.elitesm.datamanager.DataManagerException;

public class TableDoesNotExistException extends DataManagerException {
	
	public TableDoesNotExistException() {
		// TODO Auto-generated constructor stub
	}

	public TableDoesNotExistException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public TableDoesNotExistException(String message, String sourceField) {
		super(message, sourceField);
		// TODO Auto-generated constructor stub
	}

	public TableDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TableDoesNotExistException(String message, String sourceField,
			Throwable cause) {
		super(message, sourceField, cause);
		// TODO Auto-generated constructor stub
	}

	public TableDoesNotExistException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}


}
