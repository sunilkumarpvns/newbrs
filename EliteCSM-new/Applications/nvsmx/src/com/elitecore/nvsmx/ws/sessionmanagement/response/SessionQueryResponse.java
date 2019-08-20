package com.elitecore.nvsmx.ws.sessionmanagement.response;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@XmlRootElement
public class SessionQueryResponse implements WebServiceResponse {
	
	private Integer responseCode;
	private String responseMessage;
	private List<SessionData> sessionDataList;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;
	
	public SessionQueryResponse(Integer responseCode, String responseMessage, List<SessionData> sessionDataList,
			String paramter1,String paramter2,String webServiceName, String webServiceMethodName) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.sessionDataList = sessionDataList;
		this.parameter1 = paramter1;
		this.parameter1 = paramter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}

	public SessionQueryResponse() {
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
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

	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}
	
	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceName() {
		return webServiceName;
	}

	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceMethodName() {
		return webServiceMethodName;
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
