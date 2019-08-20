package com.elitecore.elitesm.ws.rest.plugins;

import com.elitecore.elitesm.ws.rest.constant.ResultCode;

public class ResponseCode {

	private String massage;
	private ResultCode resultCode;
	public String getMassage() {
		return massage;
	}
	public void setMassage(String massage) {
		this.massage = massage;
	}
	public ResultCode getResultCode() {
		return resultCode;
	}
	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}
}
