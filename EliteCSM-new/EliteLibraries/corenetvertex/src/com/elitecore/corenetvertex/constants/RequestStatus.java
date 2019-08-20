package com.elitecore.corenetvertex.constants;

public enum RequestStatus {

	QUEUE_FULL("Queue Full"),
	SUBMISSION_SUCCESSFUL("Submission Successful"),
	INVALID_PCRF_REQUEST("Invalid PCRF Request"),
	;

	private String val;
	private RequestStatus(String val){
		this.val = val;
	}

	public String getVal(){
		return val;
	}
}
