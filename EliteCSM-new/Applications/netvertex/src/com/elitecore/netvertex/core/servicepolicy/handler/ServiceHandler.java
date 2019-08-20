package com.elitecore.netvertex.core.servicepolicy.handler;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import org.apache.logging.log4j.ThreadContext;

import java.util.Objects;


/**
 * 
 * @author Manjil Purohit
 */
public abstract class ServiceHandler{
	private static final String MODULE = "SRVC-HDLR";
	private static final String DIAGNOSTIC_KEY = "HANDLER";
	
	private PCRFServiceContext serviceContext;
	private NetVertexServerContext serverContext;
	protected static final int IDEAL_EXECUTION_TIME_MS = 100;
	
	
	public ServiceHandler(PCRFServiceContext serviceContext) {
		this.serviceContext = serviceContext;
		serverContext = serviceContext.getServerContext();
	}
	
	public void init() throws InitializationFailedException {

	}
	
	
	public final void handleRequest(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext){
		try{			
			if(isApplicable(serviceRequest, serviceResponse) == false){
				return;
			}
			ThreadContext.put(DIAGNOSTIC_KEY, getName());
			PCRFRequest request = (PCRFRequest) serviceRequest;
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, getName() + " Handler execution started for Core Session ID: " + request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
			}
			
			long executionTime = System.currentTimeMillis();
			preProcess(serviceRequest, serviceResponse, executionContext);
			process(serviceRequest , serviceResponse, executionContext);
			postProcess(serviceRequest, serviceResponse,executionContext);
			executionTime = System.currentTimeMillis() - executionTime;


			PCRFResponse pcrfResponse = (PCRFResponse) serviceResponse;

			pcrfResponse.addHandlerTime(getName(), executionTime);
			if(executionTime > IDEAL_EXECUTION_TIME_MS){
				if(getLogger().isLogLevel(LogLevel.WARN))
					getLogger().warn(MODULE, getName() + " Handler execution time getting high, Last execution time = " + executionTime + " milliseconds");
			}
			

			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, getName() + " Handler execution completed for Core Session ID: " + request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
			}
			
		}catch(Exception ex){
			getLogger().error(MODULE, "Error in Handling Request in " + getName() + " Handler, Reason: " + ex.getMessage());
			getLogger().trace(ex);
		} finally {
			ThreadContext.remove(DIAGNOSTIC_KEY);
			PCRFResponse response = (PCRFResponse) serviceResponse;

			String attribute = response.getAttribute(PCRFKeyConstants.RESULT_CODE.val);
			if (Objects.nonNull(attribute)) {
				ThreadContext.put(PCRFKeyConstants.RESULT_CODE.val, attribute);
			}
		}
	}

	protected void preProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {

	}

	protected abstract void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext);
	
	protected void postProcess(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {

	}
	
	protected abstract boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse);
	
	public abstract String getName(); 
	
	public ILogger getLogger() {
		return LogManager.getLogger();
	}
	
	public NetvertexServerConfiguration getServerConfiguration() {
		return serverContext.getServerConfiguration();
	}
	
	public NetVertexServerContext getServerContext() {
		return serverContext;
	}
	
	public PCRFServiceContext getServiceContext() {
		return serviceContext;
	}
	
}
