package com.elitecore.corenetvertex.spr.exceptions;

import java.sql.SQLException;

/**
 * 
 * @author Manjil Purohit
 *
 */
public class DBOperationFailedException extends SQLException {

    private static final long serialVersionUID = 1L;

    public DBOperationFailedException(String message) {
	super(message);
    }

    public DBOperationFailedException(Throwable throwable) {
	super(throwable);
    }

    public DBOperationFailedException(String message, Throwable throwable) {
	super(message, throwable);
    }

}
