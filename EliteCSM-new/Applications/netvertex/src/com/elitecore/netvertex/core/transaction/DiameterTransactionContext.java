package com.elitecore.netvertex.core.transaction;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.TransactionSession;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

public interface DiameterTransactionContext{
	
	public TransactionSession getTransactionSession();
	public DiameterGatewayControllerContext getControllerContext();
	public RequestStatus submitPCRFRequest(PCRFRequest pcrfRequest);
	public RequestStatus submitPCRFRequest(PCRFRequest pcrfRequest, boolean bRegister);
	public TransactionState getState();
	boolean sendRequest(DiameterRequest request, String preferredPeerHostIdOrName, boolean bRegister);
	boolean sendAnswer(DiameterAnswer answer, DiameterRequest diameterRequest);
	public TimeSource getTimeSource();
	public <T> T get(String key);
	public void put(String key, Object value);
}
