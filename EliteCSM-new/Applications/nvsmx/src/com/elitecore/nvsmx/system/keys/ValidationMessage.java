package com.elitecore.nvsmx.system.keys;

/**
 * @author kirpalsinh.raj
 *
 */
public class ValidationMessage {
	
	public final static  short  SUCCESS = 1;
	public final static  short  ERROR = 0;
	
	private String message;
	private short messageCode;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(short messageCode) {
		this.messageCode = messageCode;
	}
}
