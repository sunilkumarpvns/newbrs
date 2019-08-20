package com.elitecore.corenetvertex.core.transaction;

import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;

public class TransactionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final TransactionErrorCode errorCode;
	
	public TransactionException(String message , TransactionErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public TransactionException(String message) {
		super(message);
        this.errorCode = TransactionErrorCode.UNKNOWN_ERROR;
	}
	
	public TransactionException(String message, Throwable cause) {
		super(message,cause);
        this.errorCode = TransactionErrorCode.UNKNOWN_ERROR;
	}
	
	public TransactionException(String message, Throwable cause , TransactionErrorCode errorCode) {
		super(message,cause);
		this.errorCode = errorCode;
	}
	
	public TransactionException(Throwable cause) {
		super(cause);
        this.errorCode = TransactionErrorCode.UNKNOWN_ERROR;
    }
	
	public TransactionErrorCode getErrorCode() {
		return errorCode;
	}
}
