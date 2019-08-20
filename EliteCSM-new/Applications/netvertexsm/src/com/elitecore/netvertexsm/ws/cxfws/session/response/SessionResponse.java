package com.elitecore.netvertexsm.ws.cxfws.session.response;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.elitecore.commons.io.IndentingWriter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.netvertexsm.ws.cxfws.session.data.SessionData;

/**
 * @author kirpalsinh.raj
 *
 */
public class SessionResponse{
	
	private int responseCode;
	private String responseMessage;
	private List<SessionData> sessionDataList; 
	
	public SessionResponse() { }

	
	public SessionResponse(int responseCode, String responseMessage, List<SessionData> sessionDataList) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.sessionDataList = sessionDataList;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public List<SessionData> getSessionDataList() {
		return sessionDataList;
	}

	public void setSessionDataList(List<SessionData> sessionDataList) {
		this.sessionDataList = sessionDataList;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		IndentingWriter tabbedPrintWriter = new IndentingPrintWriter(new PrintWriter(stringBuffer));
		tabbedPrintWriter.println();
		tabbedPrintWriter.println("Session Response");
		tabbedPrintWriter.incrementIndentation();
		tabbedPrintWriter.println("responseCode 	 : "+responseCode);
		tabbedPrintWriter.println("responseMessage : "+responseMessage);
		if(Collectionz.isNullOrEmpty(sessionDataList) == false){
			for(SessionData data : sessionDataList){			
				tabbedPrintWriter.println();
				tabbedPrintWriter.println("SessionData : ");
				data.toString(tabbedPrintWriter);			
			}
		}
		
		tabbedPrintWriter.decrementIndentation();
		return stringBuffer.toString();
	}
}