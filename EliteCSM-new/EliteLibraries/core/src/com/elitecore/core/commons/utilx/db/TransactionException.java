package com.elitecore.core.commons.utilx.db;

public class TransactionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private TransactionErrorCode errorCode = TransactionErrorCode.UNKNOWN_ERROR;
	
	public TransactionException(String message , TransactionErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public TransactionException(String message) {
		super(message);
	}
	
	public TransactionException(String message, Throwable cause) {
		super(message,cause);
	}
	
	public TransactionException(String message, Throwable cause , TransactionErrorCode errorCode) {
		super(message,cause);
		this.errorCode = errorCode;
	}
	
	public TransactionException(Throwable cause) {
		super(cause);
	}
	
	public TransactionErrorCode getErrorCode() {
		return errorCode;
	}
}
