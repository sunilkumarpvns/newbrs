package com.elitecore.nvsmx.remotecommunications.util;

public enum DataManagerErrorCode {
	
	CONNECTION_NOT_FOUND("Connnection Not Found"),
	UNKNOWN_ERROR("Unkown Error"), 
	;

	public final String errorCause;

	private DataManagerErrorCode(String errorCause) {
		this.errorCause = errorCause;
	}
}
