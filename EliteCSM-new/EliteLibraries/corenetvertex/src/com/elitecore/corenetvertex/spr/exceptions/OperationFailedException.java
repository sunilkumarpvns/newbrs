package com.elitecore.corenetvertex.spr.exceptions;


public class OperationFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	private ResultCode errorCode = ResultCode.INTERNAL_ERROR;

	public OperationFailedException(String message, ResultCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public OperationFailedException(String message) {
		super(message);
	}

	public OperationFailedException(Throwable throwable) {
		super(throwable);
	}
	
	public OperationFailedException(Throwable throwable, ResultCode errorCode) {
		super(throwable);
		this.errorCode = errorCode;
	}

	public OperationFailedException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public OperationFailedException(String message, ResultCode errorCode, Throwable throwable) {
		super(message,throwable);
		this.errorCode = errorCode;
	}

	public ResultCode getErrorCode() {
		return errorCode;
	}
}
