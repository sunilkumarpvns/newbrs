package com.elitecore.elitesm.ws.rest.exception;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.elitesm.ws.rest.exception.EliteResponseCode.EliteResponse;

@XmlRootElement(name = "exception-detail")
public class RestExceptionDetail {

	private String errorMessage;
	private ArrayList<String> trace;
	
	private RestExceptionDetail(){};
	
	@XmlElement(name =  "error-message")
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@XmlElementWrapper(name = "meta-data")
	@XmlElement(name =  "detail")
	public ArrayList<String> getTrace() {
		return trace;
	}

	public void setTrace(ArrayList<String> trace) {
		this.trace = trace;
	}
	
	public static EliteResponseCode getInstance( HttpServletRequest httpServletRequest,Throwable exception, String responseCode ){
		String debug = httpServletRequest.getParameter("debug");
		
		EliteResponseCode eliteResponseCode = new  EliteResponseCode();
		List<EliteResponse> eliteResponseCodeList = new ArrayList<EliteResponse>();
		
		eliteResponseCodeList.add(new EliteResponse(exception.getMessage(), responseCode));
		eliteResponseCode.setResponseCodes(eliteResponseCodeList);
		
		if(debug != null && debug.equals("true")){
			
			ArrayList<String> causeList = new ArrayList<String>();
			
			addTrace(exception,causeList,false);
			
			exception = exception.getCause();
			while (exception != null) {
				addTrace(exception,causeList,true);
				exception = exception.getCause();
			}
			
			eliteResponseCode.setTrace(causeList);
		}
		return eliteResponseCode;
	}
	
	
	private static void addTrace(Throwable exception, List<String> traceList, boolean isCause){
		StackTraceElement[] expTrace = exception.getStackTrace();
		
		if(isCause){
			traceList.add("Caused by: "+exception.getCause());
		}
		
		for (int i=0; i < expTrace.length; i++){
			traceList.add(expTrace[i].toString());
		}
	}
}
