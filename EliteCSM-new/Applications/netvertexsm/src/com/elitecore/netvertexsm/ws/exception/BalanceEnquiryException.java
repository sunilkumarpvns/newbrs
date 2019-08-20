package com.elitecore.netvertexsm.ws.exception;

public class BalanceEnquiryException extends Exception {
    
	 private long errorCode;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BalanceEnquiryException(int errorCode) {
		super();
	}
	public BalanceEnquiryException(String message,long errorCode) {
		super(message);
		this.errorCode=errorCode;
	}
	
	public BalanceEnquiryException(Throwable t,long errorCode) {
		super(t);
		this.errorCode=errorCode;
	}
	
	public BalanceEnquiryException(String message, Throwable t,long errorCode) {
		super(message,t);
		this.errorCode=errorCode;
	}
	public long getErrorCode() {
		return errorCode;
	}
	
   
	
}
