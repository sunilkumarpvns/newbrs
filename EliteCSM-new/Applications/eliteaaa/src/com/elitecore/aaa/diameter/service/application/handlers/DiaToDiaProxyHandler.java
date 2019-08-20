package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterExternalCommunicationEntryData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterSynchronousCommunicationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterSynchronousCommunicationHandlerData.ProxyMode;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import static com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterSynchronousCommunicationHandlerData.LEAVE_RESULT_CODE_AS_IS_ON_NO_ENTRY_SELECTED;

import javax.annotation.Nullable;
/*
 * TODO
 * 1) Translation failure decision
 * 2) Which session to provide in translation while response translation
 */
public class DiaToDiaProxyHandler implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {

	private static final String MODULE = "DIA-TO-DIA-PROXY-HNDLR";
	private final DiameterSynchronousCommunicationHandlerData data;
	private final DiameterServiceContext serviceContext;
	private DiameterChainHandler<DiameterFilteredHandler> handler;
	private final ITranslationAgent translationAgent;
	
	public DiaToDiaProxyHandler(DiameterServiceContext serviceContext, 
			DiameterSynchronousCommunicationHandlerData data,
			ITranslationAgent agent) {
		this.serviceContext = serviceContext;
		this.data = data;
		this.translationAgent = agent;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		if (ProxyMode.SINGLE.equals(data.getProxyMode())) {
			handler = new DiameterIfElseHandler() {
				@Override
				protected void processOnNoHandlerEligible(
						ApplicationRequest request, ApplicationResponse response) {
					DiaToDiaProxyHandler.this.processOnNoHandlerEligible(request, response);
				}
			};
		} else {
			handler = new DiameterChainHandler<DiameterFilteredHandler>() {
				@Override
				protected void processOnNoHandlerEligible(
						ApplicationRequest request, ApplicationResponse response) {
					DiaToDiaProxyHandler.this.processOnNoHandlerEligible(request, response);
				}
			};
		}
		
		for (DiameterExternalCommunicationEntryData entry : data.getEntries()) {
			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> proxyCommunicationHandler 
				= new DiaToDiaProxyCommunicationHandler(entry, serviceContext, getPeerGroupDataFor(entry), getGeoRedunduncyPeerGroupDataFor(entry), translationAgent);
			DiameterFilteredHandler filteredHandler = new DiameterFilteredHandler(entry.getRuleset(), proxyCommunicationHandler);
			filteredHandler.init();
			handler.addHandler(filteredHandler);
		}
	}

	PeerGroupData getPeerGroupDataFor(DiameterExternalCommunicationEntryData entry) {
		return serviceContext.getServerContext()
				.getServerConfiguration().getDiameterPeerGroupConfigurable()
				.getPeerGroup(entry.getPeerGroupId());
	}
	
	@Nullable PeerGroupData getGeoRedunduncyPeerGroupDataFor(DiameterExternalCommunicationEntryData entry) {
		String geoRedunduntGroupId = serviceContext.getServerContext()
				.getServerConfiguration().getDiameterPeerGroupConfigurable()
				.getPeerGroup(entry.getPeerGroupId()).getGeoRedunduntGroupId();
		if (Strings.isNullOrBlank(geoRedunduntGroupId)) {
			return null;
		}
		
		return serviceContext.getServerContext().getServerConfiguration().getDiameterPeerGroupConfigurable()
				.getPeerGroup(geoRedunduntGroupId);
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}
	
	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Handling request for policy: " 
					+ data.getPolicyName());
		}
		handler.handleRequest(request, response, session);
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	private void processOnNoHandlerEligible(ApplicationRequest request,
			ApplicationResponse response) {
		
		if (LEAVE_RESULT_CODE_AS_IS_ON_NO_ENTRY_SELECTED == data.getResultCodeOnNoEntrySelected()) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "No proxy ruleset satisfied, result code value unchanged: " 
						+ ResultCode.fromCode((int) response.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger()));
			}
			return;
		}
		
		ResultCode resultCode = ResultCode.fromCode((int) data.getResultCodeOnNoEntrySelected());
		
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, response.getDiameterAnswer(), data.getResultCodeOnNoEntrySelected() + "");
		
		if (resultCode.category.isFailureCategory) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No proxy ruleset satisfied, updating result code value to failure code: " 
						+ resultCode + ". No further handlers will be applied.");
			}
			DiameterProcessHelper.rejectResponse(response, resultCode, DiameterErrorMessageConstants.NO_RULESET_SATISFIED_FOR_PROXY_COMMUNICATION);
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No proxy ruleset satisfied, updating result code value to code: " 
						+ resultCode);
			}
		}
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return handler.isResponseBehaviorApplicable();
	}
}
