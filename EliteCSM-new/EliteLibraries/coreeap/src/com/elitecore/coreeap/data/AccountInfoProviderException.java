package com.elitecore.coreeap.data;

public class AccountInfoProviderException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private int code;
	
	public static final int DRIVER_FAILURE = 1;
	public static final int USER_NOT_FOUND = 2;
	public static final int PROFILE_OPERATION_EXCEPTION = 3;
	
	public AccountInfoProviderException() {

		super("AccountInfoProvider Exception");
	}

	public AccountInfoProviderException(String message) {
		this(message,DRIVER_FAILURE);
	}

	public AccountInfoProviderException(String message, int code) {
		super(message);	
		setCode(code);
	}

	public AccountInfoProviderException(Throwable cause) {
		this(cause,DRIVER_FAILURE);
	}

	public AccountInfoProviderException(Throwable cause, int code) {
		super(cause);
		setCode(code);
	}

	public AccountInfoProviderException(String message, Throwable cause) {
		this(message, cause,DRIVER_FAILURE);		
	}

	public AccountInfoProviderException(String message, Throwable cause, int code) {
		super(message, cause);
		setCode(code);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
