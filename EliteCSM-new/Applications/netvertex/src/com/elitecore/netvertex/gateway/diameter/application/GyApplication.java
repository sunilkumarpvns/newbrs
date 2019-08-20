package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryType;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.GySessionReleaseIndicator;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.DiameterAnswerListener;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class GyApplication extends NetvertexBaseServerApplication {

	private static final String MODULE = "GY-APP";
	private boolean isInitialized;
	private DiameterAnswerListener diameterAnswerListener;
	private List<RequestPreprocessor> preprocessor;
	private ApplicationHandler applicationHandler;
	private List<RequestValidator> validators;
	private boolean isLicenseValid=true;
	
	public GyApplication(DiameterGatewayControllerContext context,
						 @Nonnull List<RequestValidator> validators,
						 @Nonnull List<RequestPreprocessor> preProcessors,
						 ApplicationHandler applicationHandler) {
		this(context, validators, preProcessors, applicationHandler, ApplicationIdentifier.CC.getVendorId(),
				ApplicationIdentifier.CC.getApplicationId());
	}
	
	public GyApplication(DiameterGatewayControllerContext context,
						 @Nonnull List<RequestValidator> validators,
						 @Nonnull List<RequestPreprocessor> preProcessors,
						 ApplicationHandler applicationHandler, long vendorId, long applicationId) {
		super(context, vendorId,applicationId, Application.CC);
		this.validators = validators;
		this.preprocessor = preProcessors;
		this.applicationHandler = applicationHandler;
		this.diameterAnswerListener = new DiameterAnswerListenerImpl();
		context.getServerContext().registerLicenseObserver(this::checkLicenseValidity);
	}

	private void checkLicenseValidity(){
		isLicenseValid
				= context.getServerContext().isLicenseValid(LicenseNameConstants.NV_GY_INTERFACE,String.valueOf(System.currentTimeMillis()));
		if(isLicenseValid==false && LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, " License for Gy Application("+ Arrays.toString(getApplicationEnum()) +") is either not acquired or has expired.");
		}
	}
	
	@Override
	public void init() throws AppListenerInitializationFaildException {
		if (isInitialized == false) {
			superInit();
			try {
				context.registerSessionFactoryType(ApplicationIdentifier.CC.getApplicationId(),
						SessionFactoryType.INMEMORY);
			} catch (InitializationFailedException e) {
				throw new AppListenerInitializationFaildException(e);
			}
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Gy Application(" + Arrays.toString(getApplicationEnum())
						+ ") initialized successfully");
			}
			checkLicenseValidity();
			isInitialized = true;
		}
	}

	@VisibleForTesting
	void superInit() throws AppListenerInitializationFaildException {
		super.init();
	}
	
	@Override
	public void processApplicationRequest(Session session, DiameterRequest request) {
		if(isLicenseValid==false){
			DiameterAnswer answer = new DiameterAnswer(request);
			answer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.getCode());
			answer.addAvp(DiameterAVPConstants.ERROR_MESSAGE, "License expired or not acquired");

			sendAnswerSilent(session, request, answer);

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Application: Gy" +
						" is not supported. Sending " + ResultCode.DIAMETER_APPLICATION_UNSUPPORTED + " to Peer: " + request.getPeerData().getPeerName());
			}
			return;
		}

		superProcessApplicationRequest(session, request);

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Received Gy Request: " + request);
		}

		DiameterGatewayConfiguration gatewayConf = getGatewayConfiguration(request);

		if (gatewayConf == null) {
			getLogger().error(MODULE, "Unable to process Gx request with session-ID:"
					+ request.getAVPValue(DiameterAVPConstants.SESSION_ID)
					+ ". Reason: Gateway configuration not found");
			sendAnswerSilent(session, request, new DiameterAnswer(request, ResultCode.DIAMETER_UNABLE_TO_COMPLY));
			return;
		}

		for (int i = 0; i < validators.size(); i++) {
			ValidationResult validationResult = validators.get(i).validate(request);

			switch (validationResult.getResult()) {
				case SUCCESS:

					continue;
				case FAIL:

					DiameterAnswer answer = validationResult.getFailedAnswer();
					answer.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, gatewayConf.getName());
					sendAnswerSilent(session, request, answer);
					return;

				case DROP:

					return;
				default:
					break;

			}
		}

		for (int i = 0; i < preprocessor.size(); i++) {
			preprocessor.get(i).process(request, gatewayConf);
		}

		applicationHandler.handleReceivedRequest(session, request, diameterAnswerListener);
	}

	private DiameterGatewayConfiguration getGatewayConfiguration(DiameterRequest request) {
		DiameterGatewayConfiguration gatewayConf = context.getGatewayConfiguration(request
				.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME), request.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

		if (gatewayConf == null) {
			gatewayConf = context.getGatewayConfigurationByHostId(request.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
		}
		
		return gatewayConf;
	}

	@VisibleForTesting
	void superProcessApplicationRequest(Session session, DiameterRequest request) {
		super.processApplicationRequest(session, request);
	}
	
	private void sendAnswerSilent(Session session, DiameterRequest request, DiameterAnswer answer) {
		
		String sessionId = request.getAVPValue(DiameterAVPConstants.SESSION_ID);
		
		try {
			sendAnswer((DiameterSession) session, request, answer);
		} catch (CommunicationException e) {
			getLogger().error(MODULE, "Unable to send answer for request with session-ID: " + sessionId	+ ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	@Override
	public String getApplicationIdentifier() {
		return "GyApplication";
	}

	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
		return new GySessionReleaseIndicator();
	}
	
	private class DiameterAnswerListenerImpl implements DiameterAnswerListener {

		@Override
		public void answerReceived(DiameterAnswer diameterAnswer, DiameterRequest diameterRequest) throws CommunicationException {
			
			String sessionId = diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID);
			
			Session session = context.getStackContext().getOrCreateSession(sessionId, diameterRequest.getApplicationID());
			
			sendAnswer((DiameterSession)session, diameterRequest, diameterAnswer);
		}
	}

}
