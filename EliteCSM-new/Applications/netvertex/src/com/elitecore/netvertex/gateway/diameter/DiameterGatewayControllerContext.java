package com.elitecore.netvertex.gateway.diameter;

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
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.transaction.Transaction;
import com.elitecore.netvertex.gateway.GatewayMediator;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.GyTransactionType;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Diameter Gateway Context provides communication interface with Diameter Gateway Controller
 * 
 * @author Subhash Punani
 *
 */
public interface DiameterGatewayControllerContext {
	IDiameterStackContext getStackContext();
	NetVertexServerContext getServerContext();

	PolicyRepository getPolicyManager();

	SessionLocator getSessionLocator();
		
	
	RequestStatus submitPCRFRequest(PCRFRequest pcrfRequest); 
	Transaction createTransaction(String transactionType);
	Transaction createTransaction(GyTransactionType transactionType);
	void registerTransaction(String transactionId,Transaction transaction);
	Transaction removeTransaction(String transactionId);
	void sendASRtoCiscoGx(PCRFRequest pcrfResponse);
	
	boolean isSubscrberRoming(DiameterPacket diameterPacket);
	boolean isGxInterfaceEnable();
	boolean isRxInterfaceEnable();
	boolean isS9InterfaceEnable();
	DiameterGatewayConfiguration getGatewayConfiguration(String hostIdentity , String gatewayIP);
	
	DiameterGatewayConfiguration getGatewayConfigurationByHostId(String hostIdentity);
	DiameterGatewayConfiguration getGatewayConfigurationByName(String gatewayName);
	
	ApplicationHandler getApplicationHandler(Application diameterApplication, SupportedStandard supportedStandard);
	List<DiameterGroovyScript> getDiameterGroovyScripts(String hostIdentity);
	DiameterRequest buildRAR(PCRFResponse pcrfResponse);
	DiameterRequest buildASR(PCRFResponse pcrfResponse);
	void buildAAA(PCRFResponse pcrfResponse,DiameterAnswer answer);
	void buildCCA(PCRFResponse pcrfResponse,DiameterAnswer answer);
	void buildSTA(PCRFResponse pcrfResponse, DiameterAnswer answer);
	DiameterRequest buildSyRequest(PCRFResponse pcrfResponse,
			CommandCode commandCode, DiameterGatewayConfiguration configuration);

	int getOverloadResultCode();
	OverloadAction getActionOnOverload();
	boolean isCiscoGxEnabled();
	long getCurrentMessagePerMinute();
	GatewayMediator.ResultCodes reauthorizeSesion(PCRFKeyConstants pcrfKey, String sessionID, String reAuthCause, boolean forcefulReAuth,Map<PCRFKeyConstants,String> additionalParam);
	boolean sendRequest(DiameterRequest packet, @Nullable String preferredPeerHostIdOrName, Transaction transaction);
	boolean sendAnswer(DiameterAnswer packet, DiameterRequest diameterRequest);
	void handleSession(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse);
	boolean isGyInterfaceEnable();
	void registerSessionFactoryType(long appId, SessionFactoryType sessionFactoryType) throws InitializationFailedException;
}

