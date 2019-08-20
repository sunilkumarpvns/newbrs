package com.elitecore.coreeap.packet;

public class TLSAlertException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private int level;
	private int desc;
	public TLSAlertException() {
		super("TLS Alert Exception");
	}

	public TLSAlertException(String message) {
		super(message);		
	}
	
	public TLSAlertException(int level,int desc){
		this.level = level;
		this.desc = desc;
	}
	public TLSAlertException(Throwable cause) {
		super(cause);
	}

	public TLSAlertException(String message, Throwable cause) {
		super(message, cause);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getDesc() {
		return desc;
	}

	public void setDesc(int desc) {
		this.desc = desc;
	}
}
