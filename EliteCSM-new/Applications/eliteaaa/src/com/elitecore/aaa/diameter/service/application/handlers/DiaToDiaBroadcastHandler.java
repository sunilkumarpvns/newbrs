package com.elitecore.aaa.diameter.service.application.handlers;

import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterBroadcastCommunicationEntryData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterBroadcastCommunicationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterExternalCommunicationEntryData;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class DiaToDiaBroadcastHandler extends DiameterBroadcastHandler {

	private static final String MODULE = "DIA-TO-DIA-BRDCST-HNDLR";
	private ITranslationAgent translationAgent;
	private DiameterServiceContext serviceContext;

	public DiaToDiaBroadcastHandler(DiameterServiceContext context,
			DiameterBroadcastCommunicationHandlerData diameterAsynchronousCommunicationHandlerData,
			ITranslationAgent translationAgent) {
		super(context, diameterAsynchronousCommunicationHandlerData);
		this.serviceContext = context;
		this.translationAgent = translationAgent;
	}

	@Override
	protected DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> newProxyCommunicationHandler(
			DiameterBroadcastCommunicationEntryData entryData) {
		return new DiaToDiaProxyCommunicationHandler(entryData, serviceContext, 
				getPeerGroupDataFor(entryData), getGeoRedunduncyPeerGroupDataFor(entryData), 
				translationAgent);
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
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Handling request for policy: " 
					+ data.getPolicyName());
		}
		super.handleRequest(request, response, session);
	}
	
	@Override
	protected void processOnNoHandlerEligible(ApplicationRequest request, ApplicationResponse response) {
		if (DiameterBroadcastCommunicationHandlerData.LEAVE_RESULT_CODE_AS_IS_ON_NO_ENTRY_SELECTED == 
				data.getResultCodeOnNoEntrySelected()) {

			if (response.getAVP(DiameterAVPConstants.RESULT_CODE) == null) {
				LogManager.getLogger().warn(MODULE, "Ruleset not satisfied for any Broadcast entry with wait for response enabled. " +
						"Result-Code AVP is missing from packet. ");
				return;
			} else {

				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Ruleset not satisfied for any Broadcast entry with wait for response enabled, result code value unchanged: " 
							+ ResultCode.fromCode((int) response.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger()));
				}
				return;
			}
		}

		ResultCode resultCode = ResultCode.fromCode((int) data.getResultCodeOnNoEntrySelected());
		if (resultCode == null) {
			resultCode = ResultCode.fromCode((int)DiameterBroadcastCommunicationHandlerData.DEFAULT_RESULT_CODE_ON_NO_ENTRY_SELECTED);
			LogManager.getLogger().warn(MODULE, "Result code configured in 'Result code on no entry selected' = " +
					data.getResultCodeOnNoEntrySelected() + 
					", is an invalid result code. Using default result Code = " + resultCode);
		}
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, response.getDiameterAnswer(), data.getResultCodeOnNoEntrySelected() + "");
		if (resultCode.category.isFailureCategory) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Ruleset not satisfied for any Broadcast entry with wait for response enabled, updating result code value to failure code: " 
						+ resultCode + ". No further handlers will be applied.");
			}
			DiameterProcessHelper.rejectResponse(response, resultCode, DiameterErrorMessageConstants.NO_RULESET_SATISFIED_FOR_PROXY_COMMUNICATION);
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Ruleset not satisfied for any Broadcast entry with wait for response enabled, updating result code value to code: " 
						+ resultCode);
			}
		}
	}
}
