package com.elitecore.elitesm.web.core.system.dbpropertiesconfiguration;

public class DBResponse {
	
	private Boolean flag;
	private String message;
	
	public DBResponse(Boolean b, String messsage){
		this.flag = b;
		this.message = messsage;
	}
	
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
