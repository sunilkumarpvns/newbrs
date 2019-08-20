package com.elitecore.netvertex.gateway;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.service.pcrf.PCCRuleExpiryListener;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;

/**
 * <code>GatewayEventListener</code> will receive events from gateways and submit it to gateway controller for further processing
 * 
 * @author Subhash Punani
 *
 */
public abstract class GatewayEventListener {
	NetVertexServerContext serverContext;
	
	public GatewayEventListener(NetVertexServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	public void init() throws InitializationFailedException{
		
	}

	protected final NetVertexServerContext getServerContext() {
		return serverContext;
	}
	public abstract RequestStatus eventReceived(PCRFRequest request);
	public abstract RequestStatus eventReceived(PCRFRequest request,PCRFResponseListner responseListner);
	public abstract RequestStatus eventReceived(PCRFRequest request,PCRFResponseListner pcrfResponseListner,PCCRuleExpiryListener pccRuleExpiryListener);
}
