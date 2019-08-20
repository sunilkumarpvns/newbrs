package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

public class ProcessFailException extends Exception {

	private static final long serialVersionUID = 1L;
	private ResultCode errorCode = ResultCode.INTERNAL_ERROR;

	public ProcessFailException(String message, ResultCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public ProcessFailException(String message) {
		super(message);
	}

	public ProcessFailException(Throwable throwable) {
		super(throwable);
	}
	
	public ProcessFailException(Throwable throwable, ResultCode errorCode) {
		super(throwable);
		this.errorCode = errorCode;
	}

	public ProcessFailException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public ProcessFailException(String message, ResultCode errorCode, Throwable throwable) {
		super(message,throwable);
		this.errorCode = errorCode;
	}

	public ResultCode getErrorCode() {
		return errorCode;
	}
	
}
