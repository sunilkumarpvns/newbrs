package com.elitecore.netvertex.core.servicepolicy;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

/**
 * 
 * @author Manjil Purohit
 */
public abstract class ServicePolicy<T extends ServiceHandler> {
	
	private static final String MODULE = "SRV-PLC";
	
	private String ruleset;
    private ServiceContext serviceContext;
    protected List<T> serviceHandlerList;
	
	protected Expression expression;
	
	public ServicePolicy(String ruleset, ServiceContext serviceContext) {
		this.ruleset = ruleset;
        this.serviceContext = serviceContext;
        serviceHandlerList = new ArrayList<T>();
	}
	
	public void init() throws InitializationFailedException {
		try{
			if(ruleset != null && ruleset.length() !=0 ){
				Compiler compiler = Compiler.getDefaultCompiler();
				expression = compiler.parseLogicalExpression(ruleset);
			}
		}catch(InvalidExpressionException e){
			throw new InitializationFailedException(ruleset+" is "+e.getMessage() , e);
		}
	}
	
	protected abstract void preHandleRequest(ServiceRequest serviceRequest, ServiceResponse serviceResponse);
	
	protected abstract void postHandleRequest(ServiceRequest serviceRequest, ServiceResponse serviceResponse);
	
	public final ExecutionContext handleRequest(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Selected Service Policy : " + getServicePolicyName());
		
		preHandleRequest(serviceRequest, serviceResponse);
        String systemCurrency = ((NetVertexServerContext) serviceContext.getServerContext()).getServerConfiguration().getSystemParameterConfiguration().getSystemCurrency();
		ExecutionContext executionContext = new ExecutionContext((PCRFRequest)serviceRequest, (PCRFResponse)serviceResponse, CacheAwareDDFTable.getInstance(),
                systemCurrency);
		executeHandlers(executionContext, serviceResponse, serviceRequest);
		if(serviceResponse.isProcessingCompleted()){
			postHandleRequest(serviceRequest, serviceResponse);
		}
		
		return executionContext;
		
	}
	
	
	private void executeHandlers(ExecutionContext executionContext, ServiceResponse serviceResponse, ServiceRequest serviceRequest){
		int handlerIndex = executionContext.getCurrentIndex();
		
		if(handlerIndex >= serviceHandlerList.size()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping execution of service request in servicePolicy: "+ getServicePolicyName() +" with SessionID: " 
					+ ((PCRFRequest)serviceRequest).getAttribute(PCRFKeyConstants.CS_SESSION_ID.val) 
					+ ". Reason: execution handler index is greater than total Handlers");
			return;
		}
		do{
			if(!serviceResponse.isFurtherProcessingRequired())
				break;

            serviceHandlerList.get(handlerIndex).handleRequest(serviceRequest, serviceResponse, executionContext);
			handlerIndex = executionContext.incrementAndGetHandlerIndex();
		}while(handlerIndex < serviceHandlerList.size());
	}
	
	
	public final ExecutionContext resume(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext){

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Resumed Service Policy : " + getServicePolicyName());
		
		executeHandlers(executionContext, serviceResponse, serviceRequest);
		
		if(serviceResponse.isProcessingCompleted()){
			postHandleRequest(serviceRequest, serviceResponse);
		}
		
		return executionContext;
	
	}
	
	protected final void addHandler(T serviceHandler) {
		serviceHandlerList.add(serviceHandler);
	}

		
	public abstract boolean assignRequest(ServiceRequest request, ServiceResponse response);
	
	public abstract String getServicePolicyName();

	public abstract String getIdentityAttribute();

	public ServiceContext getServiceContext() {
		return serviceContext;
	}
}
