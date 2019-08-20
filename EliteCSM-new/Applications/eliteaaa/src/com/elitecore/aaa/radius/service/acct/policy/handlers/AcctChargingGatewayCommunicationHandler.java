package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.ChargingGatewayCommunicationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.core.servicex.AsyncRequestExecutor;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AcctChargingGatewayCommunicationHandler extends ChargingGatewayCommunicationHandler<RadAcctRequest, RadAcctResponse> implements RadAcctServiceHandler {
	private static final String MODULE = "ACCT-CG-COMM-HNDLR";
	
	public AcctChargingGatewayCommunicationHandler(RadAcctServiceContext serviceContext, ExternalCommunicationEntryData data) {
		super(serviceContext, data);
	}
	
	@Override
	protected AsyncRequestExecutor<RadAcctRequest, RadAcctResponse> newResponseReceivedExecutor(
			RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
		return new AcctChargingGatewayAsyncRequestExecutor();
	}

	class AcctChargingGatewayAsyncRequestExecutor implements AsyncRequestExecutor<RadAcctRequest, RadAcctResponse> {

		@Override
		public void handleServiceRequest(RadAcctRequest serviceRequest, RadAcctResponse serviceResponse) {
			//no-op -- will need to copy attributes maybe
		}
	}
	
	@Override
	protected String getModule() {
		return MODULE;
	}
}
