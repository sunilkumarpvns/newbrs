package com.elitecore.test.radius.testcase;

import com.elitecore.test.radius.testcase.data.TestCaseRequestData;
import com.elitecore.test.radius.testcase.data.TestCaseResponseData;

public class EliteRadiusTestCase {
	
	public static final String GLOBAL_SHARED_SECRET = "secret";
	
	private String testCaseId;
	private String description;
	private TestCaseRequestData testCaseRequestData;
	private TestCaseResponseData testCaseResponseData;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTestCaseId() {
		return testCaseId;
	}
	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}
	public TestCaseRequestData getTestCaseRequestData() {
		return testCaseRequestData;
	}
	public void setTestCaseRequestData(TestCaseRequestData testCaseRequestData) {
		this.testCaseRequestData = testCaseRequestData;
	}
	public TestCaseResponseData getTestCaseResponseData() {
		return testCaseResponseData;
	}
	public void setTestCaseResponseData(TestCaseResponseData testCaseResponseData) {
		this.testCaseResponseData = testCaseResponseData;
	}
	
	public byte[] getRequestDataBytes() {
		return testCaseRequestData.getBytes();
	}
	
	public byte[] getResponseDataBytes() {
		return testCaseResponseData.getBytes(testCaseRequestData.getRequestAuthenticator());
	}
}
