package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryType;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
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
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class GxApplication extends NetvertexBaseServerApplication {
	
	private static final String MODULE = "GX-APP";
	private static final  String SUBSCRIBER_ID_AVP_LIST = "subscriberIDAVPlist";
	private boolean isInitialized;
	private boolean isLicenseValid=true;
	
	
	public GxApplication(DiameterGatewayControllerContext context) {
		this(context,ApplicationIdentifier.TGPP_GX_29_212_18.getVendorId(),
				ApplicationIdentifier.TGPP_GX_29_212_18.getApplicationId());
	}
	public GxApplication(DiameterGatewayControllerContext context,
						 long vendorId, long applicationId) {
		super(context, vendorId, applicationId, Application.TGPP_GX_29_212_18);
		context.getServerContext().registerLicenseObserver(this::checkLicenseValidity);
	}

	private void checkLicenseValidity(){
		isLicenseValid
				= context.getServerContext().isLicenseValid(LicenseNameConstants.NV_GX_INTERFACE,String.valueOf(System.currentTimeMillis()));

		if(isLicenseValid==false && LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, " License for Gx Application("+ Arrays.toString(getApplicationEnum()) +") is either not acquired or has expired.");
		}
	}
	
	@Override
	public void init() throws AppListenerInitializationFaildException {
		if(!isInitialized) {
			super.init();
			try {
				context.registerSessionFactoryType(ApplicationIdentifier.TGPP_GX_29_212_18.getApplicationId(),
						SessionFactoryType.INMEMORY);
			} catch (InitializationFailedException e) {
				throw new AppListenerInitializationFaildException(e);
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Gx Application("+ Arrays.toString(getApplicationEnum()) +") initialized successfully");
			}
			checkLicenseValidity();
			isInitialized = true;
		}
	}
	
	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
		return new NVGxSessionReleaseIndiactorImpl();
	}
	
	@Override
	public void processApplicationRequest(Session session, DiameterRequest diameterRequest) {

		if(isLicenseValid==false){
			DiameterAnswer answer = new DiameterAnswer(diameterRequest);
			answer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.getCode());
			answer.addAvp(DiameterAVPConstants.ERROR_MESSAGE, "License expired or not acquired");

			sendAnswer((DiameterSession)session, diameterRequest, answer);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Application: Gx" +
						" is not supported. Sending " + ResultCode.DIAMETER_APPLICATION_UNSUPPORTED + " to Peer: " + diameterRequest.getPeerData().getPeerName());
			}
			return;
		}
		
		super.processApplicationRequest(session, diameterRequest);

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Received Gx Request: " + diameterRequest);
		
		DiameterGatewayConfiguration gatewayConfiguration = context.getGatewayConfiguration(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME) ,
				diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));
		
		if(gatewayConfiguration == null){
			gatewayConfiguration  = context.getGatewayConfigurationByHostId(diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		}
		
		if (gatewayConfiguration == null) {
			LogManager.getLogger().error(MODULE, "Unable to process Gx request with session-ID:"
					+diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
					+". Reason: Gateway configuration not found");
			DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY);

			sendAnswer((DiameterSession)session, diameterRequest, answer);

			return;
		}

		IDiameterAVP requestTypeAVP = diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);

		if (requestTypeAVP == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Unable to process Gx request with session-ID:"
				+diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
				+". Reason: Request type not found");

			DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_MISSING_AVP);
			DiameterUtility.addFailedAVP(answer, DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_REQUEST_TYPE));
			answer.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, gatewayConfiguration.getName());

			sendAnswer((DiameterSession)session, diameterRequest, answer);

			return;
		}

		if (requestTypeAVP.getInteger() != DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST
				&& requestTypeAVP.getInteger() != DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST
				&& requestTypeAVP.getInteger() != DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST) {

			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Unable to process Gx request with session-ID:"
				+diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
				+"Received invalid " + DiameterAVPConstants.CC_REQUEST_TYPE + " AVP value: " + requestTypeAVP.getStringValue());

			DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_INVALID_AVP_VALUE);
			DiameterUtility.addFailedAVP(answer, requestTypeAVP);
			answer.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, gatewayConfiguration.getName());

			sendAnswer((DiameterSession)session, diameterRequest, answer);

			return;
		}

		long currentMPM = context.getCurrentMessagePerMinute();

		long licencedMPM = context.getServerContext().getLicencedMessagePerMinute();

		if(licencedMPM > 0 && currentMPM > licencedMPM){
			if(DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST_STR.equals(requestTypeAVP.getStringValue())){


				OverloadAction overloadAction = context.getActionOnOverload();
				if(overloadAction == OverloadAction.REJECT){
					int resultCode = context.getOverloadResultCode();
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Sending "+ resultCode +" for rejected Diameter Initial Request"
								+"for Session-ID:"+session.getSessionId()+". Reason: Current load exceed valide TPS");
					}

					DiameterAnswer answer = new DiameterAnswer(diameterRequest);
					answer.addAvp(DiameterAVPConstants.RESULT_CODE, resultCode);
					answer.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, gatewayConfiguration.getName());

					sendAnswer((DiameterSession)session, diameterRequest, answer);
				} else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Dropping request with session-ID"
								+session.getSessionId()+". Reason: Current load exceed valide TPS");
					}
					context.getStackContext().updateDiameterStatsPacketDroppedStatistics(diameterRequest, diameterRequest.getRequestingHost());
				}
				return;
			}
		}

		@SuppressWarnings("unchecked")
		ArrayList<IDiameterAVP> sessSubscriberIDAVPlist = (ArrayList<IDiameterAVP>)session.getParameter(SUBSCRIBER_ID_AVP_LIST);
		if(sessSubscriberIDAVPlist==null){
			ArrayList<IDiameterAVP> subscriberIDAVPlist = diameterRequest.getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
			if(subscriberIDAVPlist != null && subscriberIDAVPlist.isEmpty()==false){
				session.setParameter(SUBSCRIBER_ID_AVP_LIST, subscriberIDAVPlist);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "SubscriberID AVPs stored into Session for sessionID= "+session.getSessionId());
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "SubscriberID AVPs missing into diameter Request for sessionID= "+session.getSessionId()+", Request-Type: "+requestTypeAVP.getStringValue());
				}
			}
		} else {

			if(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger() != DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST){
				ArrayList<IDiameterAVP> reqSubscriberIDAVPlist = diameterRequest.getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
				if(reqSubscriberIDAVPlist==null){
					diameterRequest.addAvps(sessSubscriberIDAVPlist);
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "SubscriberID AVPs added into diameter Request from Session, sessionID= "+session.getSessionId()+", Request-Type: "+requestTypeAVP.getStringValue());
					}
				}
			}
		}
		
		applyScriptsForReceivedPacket(diameterRequest, gatewayConfiguration.getName());
		
		SupportedStandard supportedStandard = gatewayConfiguration.getSupportedStandard(); 
		
		ApplicationHandler gxAppHandler = context.getApplicationHandler(Application.TGPP_GX_29_212_18, supportedStandard);
		
		if(gxAppHandler == null){
			getLogger().error(MODULE, "Unable to process Gx request with session-ID:"
					+diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
					+". Reason : No Application Handler found for Gx Application");
			DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
			answer.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, gatewayConfiguration.getName());

			sendAnswer((DiameterSession)session, diameterRequest, answer);

			return;
		}
		
		gxAppHandler.handleReceivedRequest(session, diameterRequest);
	}

	@Override
	public String getApplicationIdentifier() {
		return "GxApplication";
	}

	private class NVGxSessionReleaseIndiactorImpl extends AppDefaultSessionReleaseIndicator{

		@Override
		public boolean isEligible(DiameterPacket diameterPacket) {
			
			
			if(diameterPacket.getCommandCode() == CommandCode.ABORT_SESSION.code){
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eligible to remove session. Reason: Command-Code is ASR("+ CommandCode.ABORT_SESSION.code +")");
				return true;
			}
			
			return super.isEligible(diameterPacket);
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
