package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryType;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RxApplication extends NetvertexBaseServerApplication{
	
	private static final String MODULE = "RX-APP";
	private boolean isInitialized;
	private boolean isLicenseValid=true;
	
	public RxApplication(DiameterGatewayControllerContext context) {
		this(context, ApplicationIdentifier.TGPP_RX_29_214_18.getVendorId(),
				ApplicationIdentifier.TGPP_RX_29_214_18.getApplicationId());	
	}
	
	public RxApplication(DiameterGatewayControllerContext context, long vendorId, long applicationId) {
		super(context, vendorId, applicationId, Application.TGPP_RX_29_214_18);
		context.getServerContext().registerLicenseObserver(this::checkLicenseValidity);
	}

	private void checkLicenseValidity(){
		isLicenseValid
				= context.getServerContext().isLicenseValid(LicenseNameConstants.NV_RX_INTERFACE,String.valueOf(System.currentTimeMillis()));

		if(isLicenseValid==false && LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, " License for Rx Application("+ Arrays.toString(getApplicationEnum()) +") is either not acquired or has expired.");
		}
	}
	
	@Override
	public void init() throws AppListenerInitializationFaildException {
		if(!isInitialized) {
			super.init();
			try {
				context.registerSessionFactoryType(ApplicationIdentifier.TGPP_RX_29_214_18.getApplicationId(),
						SessionFactoryType.INMEMORY);
			} catch (InitializationFailedException e) {
				throw new AppListenerInitializationFaildException(e);
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Rx Application("+ Arrays.toString(getApplicationEnum()) + ") initialized successfully");
			}
			isInitialized = true;
		}
	}
	
	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
		return new AppDefaultSessionReleaseIndicator();
	}

	@Override
	public void processApplicationRequest(Session session, DiameterRequest diameterRequest) {
		if(isLicenseValid==false){

			DiameterAnswer answer = new DiameterAnswer(diameterRequest);
			answer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.getCode());
			answer.addAvp(DiameterAVPConstants.ERROR_MESSAGE, "License expired or not acquired");
			sendAnswer((DiameterSession)session, diameterRequest, answer);

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Application: Rx" +
						" is not supported. Sending " + ResultCode.DIAMETER_APPLICATION_UNSUPPORTED + " to Peer: " + diameterRequest.getPeerData().getPeerName());

			}
			return;
		}
		super.processApplicationRequest(session, diameterRequest);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Received Rx Request: " + diameterRequest);
		
		DiameterGatewayConfiguration gatewayConfiguration = context.getGatewayConfiguration(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME) ,
				diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

		if(gatewayConfiguration == null){
			gatewayConfiguration = context.getGatewayConfigurationByHostId(diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		}
		
		if(gatewayConfiguration == null){
			LogManager.getLogger().error(MODULE, "Unable to process Rx request with session-ID:"
									+diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
									+". Reason: Configuration not found for Gateway = " 
										+ diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
			DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY);

			sendAnswer((DiameterSession)session, diameterRequest, answer);

			return;
		}
		
		
		if(CommandCode.getCommandCode(diameterRequest.getCommandCode()) == CommandCode.AUTHENTICATION_AUTHORIZATION) {
		ArrayList<IDiameterAVP> mediaComponentDescriptions = diameterRequest.getAVPList(DiameterAVPConstants.TGPP_MEDIA_COMPONENT_DESCRIPTION);
		
		if(Collectionz.isNullOrEmpty(mediaComponentDescriptions)) {
			LogManager.getLogger().error(MODULE, "Unable to process Rx request with session-ID:"
									+diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
									+". Reason:  Media-Component-Description(" 
									+ DiameterAVPConstants.TGPP_MEDIA_COMPONENT_DESCRIPTION + ") not found in request");
			DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_MISSING_AVP);
			DiameterUtility.addFailedAVP(answer, DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_MEDIA_COMPONENT_DESCRIPTION));
			answer.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, gatewayConfiguration.getName());

			sendAnswer((DiameterSession)session, diameterRequest, answer);

			return;
		}
		}
		
		
		applyScriptsForReceivedPacket(diameterRequest, gatewayConfiguration.getName());
		
		SupportedStandard supportedStandard = gatewayConfiguration.getSupportedStandard(); 
		
		ApplicationHandler rxAppHandler = context.getApplicationHandler(Application.TGPP_RX_29_214_18, supportedStandard);
		
		if(rxAppHandler == null){
			LogManager.getLogger().error(MODULE, "Unable to process Rx request with session-ID:"
									+diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
									+". Reason : No Application Handler found for Rx Application");
			DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
			answer.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, gatewayConfiguration.getName());

			sendAnswer((DiameterSession)session, diameterRequest, answer);

			return;
		} 
		
		rxAppHandler.handleReceivedRequest(session, diameterRequest);
	}

	@Override
	public String getApplicationIdentifier() {
		return "RxApplication";
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
