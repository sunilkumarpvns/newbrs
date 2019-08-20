package com.elitecore.aaa.radius.service.acct.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.AcctAsyncRequestExecutors.AcctResponseReceivedExecutor;
import com.elitecore.aaa.radius.service.base.policy.handler.ExternalCommunicationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.AsyncRequestExecutor;

/**
 * 
 * @author narendra.pathai
 *
 */
public class RemoteAccountingHandler extends ExternalCommunicationHandler<RadAcctRequest, RadAcctResponse> implements RadAcctServiceHandler {
	private static final String MODULE = "REMOTE-ACCT-HNDLR";
	private final ExternalCommunicationEntryData data;

	public RemoteAccountingHandler(RadAcctServiceContext serviceContext, ExternalCommunicationEntryData data) {
		this(serviceContext, data, CommunicatorExceptionPolicy.ABORT);
	}
	
	public RemoteAccountingHandler(RadAcctServiceContext serviceContext, ExternalCommunicationEntryData data, CommunicatorExceptionPolicy exceptionPolicy) {
		super(serviceContext, data, exceptionPolicy);
		this.data = data;
	}

	@Override
	public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
		return true;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing Remote Accounting handler for policy: " + data.getPolicyName());
		}
		super.init();
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Remote Accounting handler for policy: " + data.getPolicyName());
		}
	}
	
	@Override
	protected AsyncRequestExecutor<RadAcctRequest, RadAcctResponse> newResponseReceivedExecutor(
			RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
		return new AcctResponseReceivedExecutor(remoteResponse);
	}
	
	@Override
	protected String getModule() {
		return MODULE;
	}
	
	@Override
	protected boolean includeInfoAttributes() {
		return false; //accounting proxy does not include proxy attributes
	}
}
