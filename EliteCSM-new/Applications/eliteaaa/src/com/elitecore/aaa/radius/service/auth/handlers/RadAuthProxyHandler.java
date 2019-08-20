package com.elitecore.aaa.radius.service.auth.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthAsyncRequestExecutors.AuthRequestTimeoutExecutor;
import com.elitecore.aaa.radius.service.auth.handlers.AuthAsyncRequestExecutors.AuthResponseReceivedExecutor;
import com.elitecore.aaa.radius.service.base.policy.handler.ExternalCommunicationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.AsyncRequestExecutor;

/**
 * This handler communicates with External Radius Authentication servers. Rejects the
 * request if remote response is Access Reject copying the reply message to the original
 * response and copies all the attributes from remote response in case of Access Accept.
 * 
 * <p>
 * This handler does not forward any info attributes to the remote external system.
 * 
 * @author narendra.pathai
 *
 */
public class RadAuthProxyHandler extends ExternalCommunicationHandler<RadAuthRequest, RadAuthResponse> implements RadAuthServiceHandler {
	private static final String MODULE = "AUTH-PROXY-HNDLR";

	public RadAuthProxyHandler(RadAuthServiceContext serviceContext, ExternalCommunicationEntryData data) {
		this(serviceContext, data, CommunicatorExceptionPolicy.ABORT);
	}
	
	public RadAuthProxyHandler(RadAuthServiceContext serviceContext, ExternalCommunicationEntryData data, CommunicatorExceptionPolicy exceptionPolicy) {
		super(serviceContext, data, exceptionPolicy);
	}
	
	@Override
	protected AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> newRequestTimeoutExecutor(
			RadUDPRequest remoteRequest) {
		return new AuthRequestTimeoutExecutor(getData().isAcceptOnTimeout());
	}
	
	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Auth Proxy Communication handler for policy: " + getData().getPolicyName());
		}
		super.init();
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Auth Proxy Communication handler for policy: " + getData().getPolicyName());
		}
	}
	
	@Override
	protected AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> newResponseReceivedExecutor(RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
		return new AuthResponseReceivedExecutor(remoteRequest, remoteResponse);
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	protected String getModule() {
		return MODULE;
	}

	@Override
	protected boolean includeInfoAttributes() {
		return false; //proxy does not send info attributes
	}
	
}
