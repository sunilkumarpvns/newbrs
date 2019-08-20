package com.elitecore.netvertex.gateway.diameter;

import java.util.List;
import java.util.Map;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.diameterapi.core.common.session.SessionFactoryType;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.transaction.Transaction;
import com.elitecore.netvertex.gateway.GatewayMediator.ResultCodes;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.GyTransactionType;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class DummyDiameterGatewayControllerContext implements DiameterGatewayControllerContext {

	private IDiameterStackContext diameterStackContext = new DummyStackContext();
	private DummyNetvertexServerContextImpl netVertexServerContext = DummyNetvertexServerContextImpl.spy();
	private PolicyRepository policyManager;
	private SessionLocator sessionLocator;
	private boolean subscriberRoaming;
	private boolean gyInterfaceEnable;
	private boolean gxInterfaceEnable;
	private boolean rxInterfaceEnable;
	private boolean s9InterfaceEnable;
	private PCRFRequest pcrfRequest;
	

	public void setDiameterStackContext(IDiameterStackContext diameterStackContext) { 
		this.diameterStackContext = diameterStackContext; 
	}

	public void setPolicyManager(PolicyRepository policyManager) { this.policyManager = policyManager; }

	public void setSessionLocator(SessionLocator sessionLocator) { this.sessionLocator = sessionLocator; }

	public void setSubscriberRoaming(boolean subscriberRoaming) { this.subscriberRoaming = subscriberRoaming; }

	public void setGyInterfaceEnable(boolean gyInterfaceEnable) { this.gyInterfaceEnable = gyInterfaceEnable; }

	public void setGxInterfaceEnable(boolean gxInterfaceEnable) { this.gxInterfaceEnable = gxInterfaceEnable; }

	public void setRxInterfaceEnable(boolean rxInterfaceEnable) { this.rxInterfaceEnable = rxInterfaceEnable; }

	public void setS9InterfaceEnable(boolean s9InterfaceEnable) { this.s9InterfaceEnable = s9InterfaceEnable; }


	@Override
	public IDiameterStackContext getStackContext() { return diameterStackContext; }

	@Override
	public PolicyRepository getPolicyManager() { return policyManager; }

	@Override
	public SessionLocator getSessionLocator() { return sessionLocator; }

	@Override
	public RequestStatus submitPCRFRequest(PCRFRequest pcrfRequest) { return null; }

	@Override
	public Transaction createTransaction(String transactionType) { return null; }

	@Override
	public void registerTransaction(String transactionId,Transaction transaction) { }

	@Override
	public Transaction removeTransaction(String transactionId) { return null; }


	@Override
	public DiameterRequest buildRAR(PCRFResponse pcrfResponse) { return null; }

	@Override
	public DiameterRequest buildASR(PCRFResponse pcrfResponse) {return null; }

	@Override
	public void buildAAA(PCRFResponse pcrfResponse, DiameterAnswer answer) {}

	@Override
	public void buildCCA(PCRFResponse pcrfResponse, DiameterAnswer answer) {}

	@Override
	public void sendASRtoCiscoGx(PCRFRequest pcrfResponse) {}

	@Override
	public void buildSTA(PCRFResponse pcrfResponse, DiameterAnswer answer) {}

	@Override
	public boolean isSubscrberRoming(DiameterPacket diameterPacket) {return subscriberRoaming;}

	@Override
	public boolean isGxInterfaceEnable() {return gxInterfaceEnable;}

	@Override
	public boolean isRxInterfaceEnable() {return rxInterfaceEnable;}

	@Override
	public boolean isS9InterfaceEnable() {return s9InterfaceEnable;}


	@Override
	public List<DiameterGroovyScript> getDiameterGroovyScripts(String hostIdentity) {return null;}

	@Override
	public DiameterRequest buildSyRequest(PCRFResponse pcrfRequest,
			CommandCode commandCode,DiameterGatewayConfiguration diameterGatewayConfiguration) {
		return null;
	}


	@Override
	public ApplicationHandler getApplicationHandler(
			Application diameterApplication, SupportedStandard supportedStandard) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPCRFRequest(PCRFRequest pcrfRequest) {
		this.pcrfRequest = pcrfRequest;
	}


	@Override
	public DiameterGatewayConfiguration getGatewayConfiguration(String hostIdentity, String gatewayIP) {
		return netVertexServerContext.getServerConfiguration().getDiameterGatewayConfByName(hostIdentity);
	}

	@Override
	public DiameterGatewayConfiguration getGatewayConfigurationByHostId(String hostIdentity) {
		return null;
	}

	@Override
	public DiameterGatewayConfiguration getGatewayConfigurationByName(String gatewayName) {
		return null;
	}

	@Override
	public DummyNetvertexServerContextImpl getServerContext() {
		return netVertexServerContext;
	}

	@Override
	public int getOverloadResultCode() {
		return 0;
	}

	@Override
	public OverloadAction getActionOnOverload() {
		return null;
	}

	@Override
	public boolean isCiscoGxEnabled() {
		return false;
	}

	@Override
	public long getCurrentMessagePerMinute() {return 0;}

	@Override
	public ResultCodes reauthorizeSesion(PCRFKeyConstants pcrfKey,
			String sessionID, String reAuth, boolean forcefulReAuth,
			Map<PCRFKeyConstants, String> additionalParam) {
		return null;
	}
	
	@Override
	public boolean sendRequest(DiameterRequest packet, String preferredPeerHostIdOrName, Transaction transaction) {
		return false;
	}

	@Override
	public boolean sendAnswer(DiameterAnswer packet, DiameterRequest diameterRequest) {
		return false;
	}

	@Override
	public void handleSession(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse) {
	
}

	@Override
	public boolean isGyInterfaceEnable() {
		return false;
	}

	@Override
	public Transaction createTransaction(GyTransactionType transactionType) {
		return null;
	}

	@Override
	public void registerSessionFactoryType(long appId, SessionFactoryType sessionFactoryType) throws InitializationFailedException {
		//no-op
	}

	
}
