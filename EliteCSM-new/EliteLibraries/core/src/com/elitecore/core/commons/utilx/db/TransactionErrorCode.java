package com.elitecore.core.commons.utilx.db;

public enum TransactionErrorCode {

	CONNECTION_NOT_FOUND("Connnection Not Found"),
	UNKNOWN_ERROR("Unkown Error"), 
	;

	public final String errorCause;

	private TransactionErrorCode(String errorCause) {
		this.errorCause = errorCause;
	}
	
	public static TransactionErrorCode fromVal(String errorCause) {
		
		if (CONNECTION_NOT_FOUND.errorCause.equalsIgnoreCase(errorCause)) {
			return CONNECTION_NOT_FOUND;
		} else if (UNKNOWN_ERROR.errorCause.equalsIgnoreCase(errorCause)) {
			return UNKNOWN_ERROR;
		} 
		
		return null;
	}
}