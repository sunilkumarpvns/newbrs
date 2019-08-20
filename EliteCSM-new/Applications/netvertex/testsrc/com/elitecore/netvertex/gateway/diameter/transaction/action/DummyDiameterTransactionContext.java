package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.gateway.diameter.transaction.session.TransactionSession;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

import static org.mockito.Mockito.spy;

public class DummyDiameterTransactionContext implements DiameterTransactionContext {

	private TransactionSession transactionSession = new TransactionSession();
	private DiameterGatewayControllerContext context = spy(new DummyDiameterGatewayControllerContext());
	public TimeSource fixedTimeSource = new FixedTimeSource();
	private PCRFRequest pcrfRequest = spy(new PCRFRequestImpl(fixedTimeSource));
	private PCRFResponse pcrfResponse = spy(new PCRFResponseImpl());
	private DiameterRequest diameterRequest = spy(new DiameterRequest());

	public DummyDiameterTransactionContext() {
		transactionSession.put(SessionKeys.PCRF_REQUEST, pcrfRequest);
		transactionSession.put(SessionKeys.PCRF_RESPONSE, pcrfResponse);
		transactionSession.put(SessionKeys.DIAMETER_REQUEST, diameterRequest);
	}

	@Override
	public TransactionSession getTransactionSession() {
		return transactionSession;
	}
	
	public void getTransactionSession(TransactionSession transactionSession) {
		this.transactionSession = transactionSession;
	}
	
	@Override
	public DiameterGatewayControllerContext getControllerContext() {
		return context;
	}
	
	public void setDiameterGatewayControllerContext(DiameterGatewayControllerContext context) {
		this.context = context;
	}

	@Override
	public RequestStatus submitPCRFRequest(PCRFRequest pcrfRequest) {
		return RequestStatus.SUBMISSION_SUCCESSFUL;
	}

	@Override
	public RequestStatus submitPCRFRequest(PCRFRequest pcrfRequest, boolean bRegister) {
		return RequestStatus.SUBMISSION_SUCCESSFUL;
	}

	@Override
	public TransactionState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sendRequest(DiameterRequest request, String preferredPeerHostIdOrName, boolean bRegister) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendAnswer(DiameterAnswer answer, DiameterRequest diameterRequest) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TimeSource getTimeSource() {
		return fixedTimeSource;
	}

	@Override
	public <T> T get(String key) {
		return getTransactionSession().get(key);
	}

	@Override
	public void put(String key, Object value) {
		getTransactionSession().put(key, value);
	}


	public DiameterRequest getDiameterRequest() {
		return get(SessionKeys.DIAMETER_REQUEST);
	}

	public PCRFRequest getPCRFRequest() {
		return get(SessionKeys.PCRF_REQUEST);
	}

	public void setTimeSource(TimeSource timeSourceChain) {
		this.fixedTimeSource = timeSourceChain;
	}
}
