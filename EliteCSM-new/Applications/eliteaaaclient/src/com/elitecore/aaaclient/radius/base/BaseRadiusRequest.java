package com.elitecore.aaaclient.radius.base;

import com.elitecore.aaaclient.radius.IRadiusRequest;

public abstract class BaseRadiusRequest implements IRadiusRequest {
	
	public static final int EXE_NOT_STARTED_CODE = 0;
	public static final String EXE_NOT_STARTED_TEXT = "Not Started";
	
	public String serverAddress;
	public int requestPort;
	public int retryCount;
	public int requestTimeout;
	public int requestTypeId;
	public String testCaseName;
	public int executionStatus;
	public int finalResult;
	
	public String sharedSecret;

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getRequestPort() {
		return requestPort;
	}

	public void setRequestPort(int requestPort) {
		this.requestPort = requestPort;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public int getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public String getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	public int getRequestTypeId() {
		return requestTypeId;
	}

	public void setRequestTypeId(int requestTypeId) {
		this.requestTypeId = requestTypeId;
	}

	public String getRequestTypeString() {
		return requestTypeId == 1? "Authentication Request (1)" : requestTypeId == 2 ? "Accounting Request (2)" : "Custom Request (" + requestTypeId +")";
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public int getExecutionStatus() {
		return executionStatus;
	}

	public String getExecutionStatusText() {
		if (executionStatus == EXE_NOT_STARTED_CODE)
			return EXE_NOT_STARTED_TEXT;
		
		return "Unknown";
	}
	
	public void setExecutionStatus(int executionStatus) {
		this.executionStatus = executionStatus;
	}
	
	
	public int getFinalResult() {
		return finalResult;
	}

	public void setFinalResult(int finalResult) {
		this.finalResult = finalResult;
	}

	public String getFinalResultText() {
		return "NA";
	}

}
