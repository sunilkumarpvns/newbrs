package com.elitecore.core.commons.util.db.errorcodes;

/**
 * 
 * @author Malav Desai
 * 
 */
public enum EliteErrorCodes {
	CONNECTION_POOL_FULL_ERROR(-1, "Database connection pool full", false),
	UNKNOWN_ERROR(-255, "Unexpected Error", false);
	
	private final int errorCode;
	private final String errorMessage;
	private final boolean isDBDown;
	
	private EliteErrorCodes(int errorCode, String errorMessage, boolean isDBDown) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.isDBDown = isDBDown;
	}
	
	public int getErrorCode(){
		return this.errorCode;
	}
	
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	public boolean isDBDown(){
		return this.isDBDown;
	}
}