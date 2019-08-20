package com.elitecore.netvertex.service.pcrf;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import org.apache.logging.log4j.ThreadContext;

import java.util.Map;

public class SyHandlerResponseListener implements PCRFResponseListner{
	
	private static final String MODULE = "SY-RES-LSTR";
	private PCRFRequest pcrfRequest;
	private PCRFResponse pcrfResponse;
	private ExecutionContext executionContext;
	private PCRFServiceContext pcrfServiceContext;
	private Map<String, String> contextInformation;

	public SyHandlerResponseListener(PCRFRequest pcrfRequest,
									 PCRFResponse pcrfResponse,
									 ExecutionContext executionContext,
									 PCRFServiceContext pcrfServiceContext, Map<String, String> contextInformation){
		this.pcrfRequest = pcrfRequest;
		this.pcrfResponse = pcrfResponse;
		this.executionContext = executionContext;
		this.pcrfServiceContext = pcrfServiceContext;
		this.contextInformation = contextInformation;
	}


	@Override
	public void responseReceived(PCRFResponse response) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "PCRF response for Sy application is received: " + response);
		
		pcrfResponse.setFurtherProcessingRequired(true);
		pcrfResponse.setProcessingCompleted(true);
		ThreadContext.putAll(contextInformation);
		pcrfServiceContext.resume(pcrfRequest, pcrfResponse,
									executionContext);
	}
	
	public PCRFResponse getPCRFResponse(){
		return pcrfResponse;
	}
	
	public void requestTimeout(){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Resume processing for PCRF request with core-session-Id: " 
								+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) +". Reason: Timeout occurred for SLR");
		
		pcrfResponse.setFurtherProcessingRequired(true);
		pcrfResponse.setProcessingCompleted(true);
		pcrfResponse.setAttribute(PCRFKeyConstants.SY_COMMUNICATION.val, PCRFKeyValueConstants.SY_COMMUNICATION_TIMEOUT.val);
		ThreadContext.putAll(contextInformation);
		pcrfServiceContext.resume(pcrfRequest, pcrfResponse,
									executionContext);
	}
}