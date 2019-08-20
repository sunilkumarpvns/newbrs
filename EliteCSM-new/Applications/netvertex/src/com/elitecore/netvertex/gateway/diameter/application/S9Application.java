package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryType;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.netvertex.core.transaction.EventTypes;
import com.elitecore.netvertex.core.transaction.Transaction;
import com.elitecore.netvertex.core.transaction.TransactionType;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class S9Application extends NetvertexBaseServerApplication {

	private static final String MODULE = "S9-APP";
	private boolean isInitialized;
	private boolean isLicenseValid=true;
	
	public S9Application(DiameterGatewayControllerContext context) {
		this(context,ApplicationIdentifier.TGPP_S9.getVendorId(),ApplicationIdentifier.TGPP_S9.getApplicationId(), ApplicationIdentifier.TGPP_S9.getApplication());
	}
	
	public S9Application(DiameterGatewayControllerContext context, long vendorId, long applicationId, Application application) {
		super(context, vendorId, applicationId, application);
		context.getServerContext().registerLicenseObserver(this::checkLicenseValidity);
	}

	private void checkLicenseValidity(){
		isLicenseValid
				= context.getServerContext().isLicenseValid(LicenseNameConstants.NV_S9_INTERFACE,String.valueOf(System.currentTimeMillis()));

		if(isLicenseValid==false && LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, " License for S9 Application("+ Arrays.toString(getApplicationEnum()) +") is either not acquired or has expired.");
		}
	}

	@Override
	public void init() throws AppListenerInitializationFaildException {
		if(!isInitialized) {
			super.init();
			try {
				context.registerSessionFactoryType(ApplicationIdentifier.TGPP_S9.getApplicationId(),
						SessionFactoryType.INMEMORY);
			} catch (InitializationFailedException e) {
				throw new AppListenerInitializationFaildException(e);
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "S9 Application("+ Arrays.toString(getApplicationEnum()) +") initialized successfully");
			}
			checkLicenseValidity();
			isInitialized = true;
		}
	}

	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
		return new AppDefaultSessionReleaseIndicator();
	}

	@Override
	public String getApplicationIdentifier() {
		return "S9Application";
	}

	@Override
	public void processApplicationRequest(Session session, DiameterRequest diameterRequest) {

		if(isLicenseValid==false){
			DiameterAnswer answer = new DiameterAnswer(diameterRequest);
			answer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.getCode());
			answer.addAvp(DiameterAVPConstants.ERROR_MESSAGE, "License expired or not acquired");
			sendAnswer((DiameterSession)session, diameterRequest, answer);

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Application: S9" +
						" is not supported. Sending " + ResultCode.DIAMETER_APPLICATION_UNSUPPORTED + " to Peer: " + diameterRequest.getPeerData().getPeerName());
			}
			return;
		}
		super.processApplicationRequest(session, diameterRequest);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Received S9 Request: " + diameterRequest);
		
		if(CommandCode.CREDIT_CONTROL.code != diameterRequest.getCommandCode()){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Unable to process S9 Reqeust. Reason: Unsupported command-code = " + diameterRequest.getCommandCode());
			return;
		}

		IDiameterAVP diameterAVP  = diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		
		if(diameterAVP == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error in processing diameter request. Reason: Request-Type avp not found");
			return;
		}
	
		DiameterGatewayConfiguration gatewayConfiguration = context.getGatewayConfiguration(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME) ,
				diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

		if(gatewayConfiguration == null){
			gatewayConfiguration = context.getGatewayConfigurationByHostId(diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		}
		
		if(gatewayConfiguration == null){
			LogManager.getLogger().error(MODULE, "Unable to process S9 request. Reason: Gateway configuration not found");
			return;
		}
	
		applyScriptsForReceivedPacket(diameterRequest, gatewayConfiguration.getName());
		
		String transactionType = null;
		String eventType = null;
		if(diameterAVP.getInteger() == DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST){
			// Session Start Request
			transactionType = TransactionType.SESSION_START;
			eventType = EventTypes.SESSION_START;
		}else if(diameterAVP.getInteger() == DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST){
			transactionType = TransactionType.SESSION_UPDATE;
			eventType = EventTypes.SESSION_UPDATE;
			
			IDiameterAVP sessionEnforcementAVP = diameterRequest.getAVP(DiameterAVPConstants.TGPP_SESSION_ENFORCEMENT_INFO);
			List<IDiameterAVP> chargingRuleReportAVPs = null;
			if(sessionEnforcementAVP != null){
				List<IDiameterAVP> diameterAVPs = ((AvpGrouped)sessionEnforcementAVP).getSubAttributeList(DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT);
				if(diameterAVPs != null && !diameterAVPs.isEmpty()){
					chargingRuleReportAVPs = new ArrayList<IDiameterAVP>(diameterAVPs);
				}
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Unable to check for Charging-Rule-Report("+ DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT +") AVP from packet. Reason: Session-Enforcement-info("+ DiameterAVPConstants.TGPP_SESSION_ENFORCEMENT_INFO +") AVP not found in packet of S9Application");
			}
			
			if(chargingRuleReportAVPs != null && !chargingRuleReportAVPs.isEmpty()){
				for(IDiameterAVP chargingRuleReportAVP : chargingRuleReportAVPs){
					IDiameterAVP chargingRuleStatusAVP = ((AvpGrouped)chargingRuleReportAVP).getSubAttribute(DiameterAVPConstants.TGPP_PCC_RULE_STATUS);
					if(chargingRuleStatusAVP != null && chargingRuleStatusAVP.getInteger() == DiameterAttributeValueConstants.TGPP_PCC_RULE_STATUS_INACTIVE){
						transactionType = TransactionType.RULE_REMOVE;
						eventType = EventTypes.RULE_REMOVED;
						break;
					}
				}
			}
			
		}else if(diameterAVP.getInteger() == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST){
			Transaction transaction = context.createTransaction(TransactionType.SESSION_STOP);
			transaction.process(EventTypes.SESSION_STOP,diameterRequest);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Skipping further processing. Reason: Unsupported request-type for Gx request");
			return;
		}
		
		if(transactionType != null && eventType != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Create " + transactionType + " transaction for " +  eventType + " transaction event");
			Transaction transaction = context.createTransaction(transactionType);
			transaction.process(eventType,diameterRequest);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Skipping further processing. Reason: Unsupported event for gx requst");
			return;
		}
		
	}

	@Override
	public void sendAnswer(DiameterSession session, DiameterRequest request, DiameterAnswer answer) {
		try {
			super.sendAnswer(session, request, answer);
		} catch (CommunicationException e) {
			getLogger().error(MODULE, "Unable to send answer for request with session-ID:"
					+request.getAVPValue(DiameterAVPConstants.SESSION_ID)
					+". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

}
