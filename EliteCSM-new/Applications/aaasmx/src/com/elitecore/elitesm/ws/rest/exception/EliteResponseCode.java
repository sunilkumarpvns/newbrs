package com.elitecore.elitesm.ws.rest.exception;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;

/**
 * Response Code will wrap REST response to custom response.
 * It contains response message and response code with it.<br/><br/>
 * 
 * <b>e.g :</b><br/><br/>
 * 
 * CASE 1 : Failure <br>
 * 
 * <pre>
 *  {
 * 	  "response": [
 *   		{
 *    		"response-message": "Peer name must be specified.",
 *     		"response-code": "401"
 *  		}
 * 	 	]
 *	}
 * </pre>
 * 
 *  CASE 2 : Success <br>
 * 
 * <pre>
 *  {
 * 	  "response": [
 *   		{
 *    		"response-message": "Diameter Peer created successfully.",
 *     		"response-code": "200"
 *  		}
 * 	 	]
 *	}
 * </pre>
 * @author nayana.rathod
 * @param <T>
 *
 */

@XmlRootElement(name = "response-detail")
@XmlType(propOrder = { "responseCodes", "trace" })
public class EliteResponseCode {

	private List<EliteResponse> responseCodes;
	
	private ArrayList<String> trace;

	public EliteResponseCode() {
		responseCodes = new ArrayList<EliteResponse>();
	}

	@XmlElement(name = "response")
	public List<EliteResponse> getResponseCodes() {
		return responseCodes;
	}

	public void setResponseCodes(List<EliteResponse> responseCodes) {
		this.responseCodes = responseCodes;
	}

	@XmlElementWrapper(name = "meta-data")
	@XmlElement(name =  "detail")
	public ArrayList<String> getTrace() {
		return trace;
	}

	public void setTrace(ArrayList<String> trace) {
		this.trace = trace;
	}

	@XmlType(propOrder = { "responseCode", "responseMessage","responseTime","succeedRecords","failedRecords","responseBodyData" })
	public static class EliteResponse {

		private String responseMessage;

		private String responseCode = ResultCode.INTERNAL_ERROR.responseCode;
		
		private String responseTime;
		
		private String succeedRecords;
		
		private String failedRecords;
		
		private List<Status> responseBodyData; 
		
		public EliteResponse(String message, String errorCode) {
			this.responseMessage = message;
			this.setResponseCode(errorCode);
		}
		
		public EliteResponse(String message, String errorCode, String responseTime) {
			this.responseMessage = message;
			this.setResponseCode(errorCode);
			this.responseTime = responseTime;
		}
		
		public EliteResponse(String message, String errorCode, String successedRecord, String failedRecord){
			this.responseMessage = message;
			this.responseCode = errorCode;
			this.succeedRecords = successedRecord;
			this.failedRecords = failedRecord;
		}
		
		public EliteResponse(String message, String errorCode, List<Status> responseBodyData, String successedRecord, String failedRecord) {
			this.responseMessage = message;
			this.responseCode = errorCode;
			this.responseBodyData = responseBodyData;
			this.succeedRecords = successedRecord;
			this.failedRecords = failedRecord;
		}
		
		@XmlElement(name = "response-message")
		public String getResponseMessage() {
			return responseMessage;
		}

		public void setResponseMessage(String responseMessage) {
			this.responseMessage = responseMessage;
		}

		@XmlElement(name = "response-code")
		public String getResponseCode() {
			return responseCode;
		}

		public void setResponseCode(String responseCode) {
			this.responseCode = responseCode;
		}

		@XmlElement(name = "response-time")
		public String getResponseTime() {
			return responseTime;
		}

		public void setResponseTime(String responseTime) {
			this.responseTime = responseTime;
		}

		@XmlElementWrapper(name = "body")
		@XmlElement(name = "response")
		public List<Status> getResponseBodyData() {
			return responseBodyData;
		}

		public void setResponseBodyData(List<Status> responseBodyData) {
			this.responseBodyData = responseBodyData;
		}

		@XmlElement(name = "succeed-records")
		public String getSucceedRecords() {
			return succeedRecords;
		}

		public void setSucceedRecords(String succeedRecords) {
			this.succeedRecords = succeedRecords;
		}

		@XmlElement(name = "failed-records")
		public String getFailedRecords() {
			return failedRecords;
		}

		public void setFailedRecords(String failedRecords) {
			this.failedRecords = failedRecords;
		}
	}
}
