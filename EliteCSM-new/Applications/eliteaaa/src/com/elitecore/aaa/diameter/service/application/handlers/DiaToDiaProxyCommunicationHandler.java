package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.aaa.diameter.service.application.handlers.DiameterBroadcastHandler.HIGHEST_PRIORITY_NEGATIVE_ANSWER;
import static com.elitecore.aaa.diameter.service.application.handlers.DiameterBroadcastHandler.HIGHEST_PRIORITY_RESULT_CODE;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterExternalCommunicationEntryData;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.peer.exception.PeerNotFoundException;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerCommGroup;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupFactory;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.core.common.peer.group.GeoRedunduncyPeerGroup;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.session.ServerInitiatedRequestResponseListener;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;

// TODO check dummy translation mapping flow.
// TODO we can utilize DIAMTER_SESSION_UNKNOWN result code in case when RAR cannot be delivered. Need to verify
// TODO original application response should be rejected in case when translation failure occurs (similar situations) and further handler processing should not occur. Check wherever aggregation occurs we need to check result code category for failure and set further processing to be false.
class DiaToDiaProxyCommunicationHandler implements DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> {

	private static final String MODULE = "PXY-COMM-HDLR";
	@Nonnull private DiameterExternalCommunicationEntryData data;
	@Nonnull private DiameterServiceContext serviceContext;
	@Nonnull private ITranslationAgent translationAgent;
	@Nonnull private final PeerGroupData groupData;
	@Nullable private final PeerGroupData geoRedunduncyPeerGroupData;
	private DiameterPeerGroupFactory groupFactory;
	private DiameterPeerCommGroup group;

	public DiaToDiaProxyCommunicationHandler(@Nonnull DiameterExternalCommunicationEntryData data, 
			@Nonnull DiameterServiceContext serviceContext,
			@Nonnull PeerGroupData groupData,
			@Nullable PeerGroupData geoRedunduncyPeerGroupData, 
			@Nonnull ITranslationAgent translationAgent) {
		this.geoRedunduncyPeerGroupData = geoRedunduncyPeerGroupData;
		this.data = checkNotNull(data, "entry data is null");
		this.serviceContext = checkNotNull(serviceContext, "serviceContext is null");
		this.groupData = checkNotNull(groupData, "groupData is null");
		this.translationAgent = checkNotNull(translationAgent, "translationAgent is null");
		this.groupFactory = new DiameterPeerGroupFactory(serviceContext.getStackContext());
	}

	@Override
	public void init() throws InitializationFailedException {

		if (Strings.isNullOrBlank(data.getTranslationMapping()) == false && 
				translationAgent.isExists(data.getTranslationMapping()) == false) {
			throw new InitializationFailedException("Translation Mapping: " + data.getTranslationMapping() 
			+ " is not registered.");
		}

		createGroup();
	}

	void createGroup() throws InitializationFailedException {
		DiameterPeerCommGroup primaryGroup = createLoadBalancingGroup(groupData);
		if (geoRedunduncyPeerGroupData != null) {
			DiameterPeerCommGroup geoRedunduntGroup = createLoadBalancingGroup(geoRedunduncyPeerGroupData);
			group = new GeoRedunduncyPeerGroup(primaryGroup, geoRedunduntGroup);
		} else {
			group  = primaryGroup;
		}
	}

	private DiameterPeerCommGroup createLoadBalancingGroup(PeerGroupData groupData) throws InitializationFailedException {
		Map<String, Integer> peers = new LinkedHashMap<String, Integer>();
		for (PeerInfoImpl peerInfo : groupData.getPeers()) {
			peers.put(peerInfo.getPeerName(), peerInfo.getLoadFactor());
		}
		
		DiameterPeerGroupParameter groupParameter = new DiameterPeerGroupParameter(groupData.getName(), peers, 
				LoadBalancerType.ROUND_ROBIN, groupData.isStateFull(), groupData.getRetryLimit(), 
				groupData.getTransactionTimeoutInMs());
		try {
			return groupFactory.getInstance(groupParameter); 
		} catch (PeerNotFoundException e) {
			throw new InitializationFailedException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}

	@Override
	public void handleRequest(final ApplicationRequest request, final ApplicationResponse response, ISession diameterSession) {
		DiameterSession session = (DiameterSession) diameterSession;
		if (session == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending " + ResultCode.DIAMETER_UNKNOWN_SESSION_ID 
						+ ", Reason: Diameter session with id: " + request.getDiameterRequest().getSessionID() + " not found.");
			}
			
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_UNKNOWN_SESSION_ID, DiameterErrorMessageConstants.DIAMETER_SESSION_NOT_FOUND);
			return;
		}
		
		TranslationHelper translationHelper = new TranslationHelper(request.getDiameterRequest(), response.getDiameterAnswer());

		DiameterRequest remoteRequest;
		try {
			remoteRequest = translationHelper.translateRequest(session);
		} catch (TranslationFailedException e) {
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_UNABLE_TO_COMPLY, 
					DiameterErrorMessageConstants.translationFailed(data.getTranslationMapping()));
			return;
		}

		if (translationHelper.isDummyMappingApplicable()) {
			DiameterAnswer remoteAnswer = translationHelper.getDummyAnswer();

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Dummy response: " + remoteAnswer);
			}
			DiameterAnswer translatedAnswer = DiameterUtility.newAnswerWithHeaderOf(remoteRequest);
			try {
				translatedAnswer = translationHelper.translateAnswer(remoteAnswer, translatedAnswer, session);
				aggregate(response, translatedAnswer);
			} catch (TranslationFailedException e) {
				DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_UNABLE_TO_COMPLY, 
						DiameterErrorMessageConstants.translationFailed(data.getTranslationMapping()));
			}
			return;
		}

		addRouteRecoredAVP(request.getDiameterRequest(), remoteRequest);
		
		try {
			DiameterProcessHelper.onExternalCommunication(request, response);

			if (request.getDiameterRequest().isServerInitiated() == false) {
				group.sendClientInitiatedRequest(session, remoteRequest, new ProxyResponseListener(request, response, remoteRequest, translationHelper));
			} else {
				handleServerInitiatedRequest(request, response, session, translationHelper, remoteRequest);
 			}

		} catch (CommunicationException ex) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER 
						+ ", Reason: " + ex.getMessage());
			}
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_UNABLE_TO_DELIVER, 
					DiameterErrorMessageConstants.DEST_PEER_NOT_FOUND);
		}
	}

	private void handleServerInitiatedRequest(final ApplicationRequest request, final ApplicationResponse response,
			final DiameterSession session, TranslationHelper translationHelper, DiameterRequest remoteRequest)
					throws CommunicationException {
		try {
			String destinationPeer = selectServerInitiatedPeer(session, request);
			
			serviceContext.getStackContext().getPeerCommunicator(destinationPeer).sendServerInitiatedRequest(session, remoteRequest,
					new ServerInitiatedRequestResponseListener(new ProxyResponseListener(request, response, remoteRequest, translationHelper)));
		} catch (PeerNotFoundException ex) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER 
						+ ", Reason: " + ex.getMessage());
			}
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_UNABLE_TO_DELIVER, 
					DiameterErrorMessageConstants.DEST_PEER_NOT_FOUND);
		}
	}


	private @Nonnull String selectServerInitiatedPeer(DiameterSession session, ApplicationRequest request) {
		assert request.getDiameterRequest().isServerInitiated();

		String destinationHost = request.getDiameterRequest().getAVPValue(DiameterAVPConstants.DESTINATION_HOST);
		if (destinationHost != null) {
			PeerData peerData = serviceContext.getStackContext().getPeerData(destinationHost);
			if (peerData != null) {
				return destinationHost;
			}
			
			throw new PeerNotFoundException("Destination Host: " + destinationHost + " received in request is unknown");
		}
		
		String preferredPeer = (String) session.getParameter(DiameterAVPConstants.ORIGIN_HOST);
		if (preferredPeer != null) {
			return preferredPeer;
		}
		
		throw new PeerNotFoundException("Cannot select destination peer for server initiated request");
	}

	@Override
	public void handleAsyncRequest(final ApplicationRequest request, 
			final ApplicationResponse response,
			ISession session, final DiameterBroadcastResponseListener listener) {

		final int order = listener.getNextOrder();
		final DiameterSession diameterSession = (DiameterSession) session;
		if (session == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending " + ResultCode.DIAMETER_UNKNOWN_SESSION_ID 
						+ ", Reason: Diameter session with id: " + request.getDiameterRequest().getSessionID() + " not found.");
			}
			
			final DiameterAnswer unknownSessionIdAnswer = new DiameterAnswer();
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, unknownSessionIdAnswer, ResultCode.DIAMETER_UNKNOWN_SESSION_ID.code + "");
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, unknownSessionIdAnswer, DiameterErrorMessageConstants.DIAMETER_SESSION_NOT_FOUND);
			unknownSessionIdAnswer.addAvp(DiameterAVPConstants.SESSION_ID, request.getDiameterRequest().getSessionID());
			
			listener.addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor.from(
					new DiameterAsyncRequestExecutor() {

				@Override
				public void handleServiceRequest(ApplicationRequest serviceRequest,
						ApplicationResponse serviceResponse, ISession session) {
					aggregate(serviceResponse, unknownSessionIdAnswer);
				}
			}, order));

			listener.responseReceived(null, unknownSessionIdAnswer, session);
			return;
		}
		
		TranslationHelper translationHelper = new TranslationHelper(request.getDiameterRequest(), response.getDiameterAnswer());

		DiameterRequest remoteRequest;
		try {
			remoteRequest = translationHelper.translateRequest(diameterSession);
		} catch (TranslationFailedException e) {

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_COMPLY 
						+ ", Reason: " + e.getMessage());
			}
			final DiameterAnswer remoteAnswer = new DiameterAnswer();
			
			remoteAnswer.addAvp(DiameterAVPConstants.SESSION_ID, request.getDiameterRequest().getSessionID());
			translationHelper.onTranslationFailure(remoteAnswer);
			
			listener.addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor.from(
					new DiameterAsyncRequestExecutor() {

				@Override
				public void handleServiceRequest(ApplicationRequest serviceRequest,
						ApplicationResponse serviceResponse, ISession session) {
					aggregate(serviceResponse, remoteAnswer);
				}
			}, order));

			//TODO Radius sends source request packet untranslated, What should we do here?
			listener.responseReceived(null, remoteAnswer, session);
			return;
		}

		final TranslatingBroadcastResponseListener translatingBroadcastResponseListener = new TranslatingBroadcastResponseListener(listener,
				request, response, translationHelper, remoteRequest, order);

		if (translationHelper.isDummyMappingApplicable()) {

			DiameterAnswer remoteAnswer = translationHelper.getDummyAnswer();

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Dummy response: " + remoteAnswer);
			}

			translatingBroadcastResponseListener.responseReceived(remoteAnswer, Parameter.getInstance().getOwnDiameterIdentity(), diameterSession);

			return;
		}

		addRouteRecoredAVP(request.getDiameterRequest(), remoteRequest);
		
		try {
			if (request.getDiameterRequest().isServerInitiated() == false) {
				group.sendClientInitiatedRequest(diameterSession, remoteRequest, translatingBroadcastResponseListener);
			} else {
				serviceContext.getStackContext().getPeerCommunicator(selectServerInitiatedPeer(diameterSession, request)).sendServerInitiatedRequest(diameterSession, remoteRequest, 
						translatingBroadcastResponseListener);
			}
		} catch (CommunicationException ex) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER 
						+ ", Reason: " + ex.getMessage());
			}

			DiameterAnswer remoteDiameterAnswer = new DiameterAnswer(remoteRequest,
					ResultCode.DIAMETER_UNABLE_TO_DELIVER);
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, remoteDiameterAnswer, DiameterErrorMessageConstants.DEST_PEER_NOT_FOUND);
			translatingBroadcastResponseListener.responseReceived(remoteDiameterAnswer, Parameter.getInstance().getOwnDiameterIdentity(), diameterSession);
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}

	private class ProxyResponseListener implements ResponseListener {
		private static final String MODULE = "PXY-RES-LIST";
		private final ApplicationRequest originalRequest;
		private final ApplicationResponse originalResponse;
		private TranslationHelper translationHelper;
		private DiameterRequest remoteDiameterRequest;

		public ProxyResponseListener( 
				ApplicationRequest originalRequest, ApplicationResponse originalResponse,
				DiameterRequest remoteDiameterRequest, TranslationHelper translationHelper) {
			this.originalRequest = originalRequest;
			this.originalResponse = originalResponse;
			this.remoteDiameterRequest = remoteDiameterRequest;
			this.translationHelper = translationHelper;
		}

		@Override
		public void responseReceived(final DiameterAnswer answer, String hostIdentity, DiameterSession originatorSession) {
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Diameter answer received with Session-Id=" 
						+ answer.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
			DiameterAnswer translatedAnswer = DiameterUtility.newAnswerWithHeaderOf(remoteDiameterRequest);
			try {
				translatedAnswer = translationHelper.translateAnswer(answer, translatedAnswer, originatorSession);
			} catch (TranslationFailedException e) {
				translationHelper.onTranslationFailure(translatedAnswer);
			}
			aggregate(originalResponse, translatedAnswer);
			serviceContext.resumeRequestInSync(originatorSession, originalRequest, originalResponse);
		}

		@Override
		public void requestTimedout(String hostIdentity, DiameterSession originatorSession) {
			
			if (data.isAcceptableResultCode(ResultCode.DIAMETER_REQUEST_TIMEOUT.code) == false) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Timeout occured for request with Session-Id=" 
							+ remoteDiameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) 
							+ ", sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER 
							+ ", as accept on timeout is disabled.");
				}

				DiameterProcessHelper.rejectResponse(originalResponse, ResultCode.DIAMETER_UNABLE_TO_DELIVER, DiameterErrorMessageConstants.REQUEST_TIMEOUT);

			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Timeout occured for request with Session-Id=" 
							+ remoteDiameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) 
							+ ", sending " + ResultCode.DIAMETER_SUCCESS 
							+ ", as accept on timeout is enabled.");
				}						
			}
			serviceContext.resumeRequestInAsync(originatorSession, originalRequest, originalResponse);
		}
	}

	private void aggregate(ApplicationResponse originalResponse, DiameterAnswer translatedAnswer) {
		if(LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Aggregating answer received from : " + translatedAnswer.getAnsweringHost());
		}
		
		IDiameterAVP resultCodeAVP = translatedAnswer.getAVP(DiameterAVPConstants.RESULT_CODE);
		if(resultCodeAVP == null) {
			LogManager.getLogger().warn(MODULE, "Result-code AVP not found in translated remote response, using " + ResultCode.DIAMETER_SUCCESS.code + " as default value");
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, translatedAnswer, String.valueOf(ResultCode.DIAMETER_SUCCESS.code));
			resultCodeAVP = translatedAnswer.getAVP(DiameterAVPConstants.RESULT_CODE);
		}

		int resultCode = (int) resultCodeAVP.getInteger();
		
		if (data.isAcceptableResultCode(resultCode)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE,"Ignoring answer packet received from " +
						translatedAnswer.getAnsweringHost() + ", Reason: " + 
						"result code configured in answer packet is " +
						"configured in acceptable result code field.");
			}
			return;
		}
		
		if(data.getPriorityResultCodes().isEmpty() == false) {
			if(data.getPriorityResultCodes().contains(resultCode) == false) {
				if(data.getPriorityResultCodes().contains(ResultCodeCategory.getResultCodeCategory(resultCode).value) == false) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE,"Ignoring answer packet received from " +
								translatedAnswer.getAnsweringHost()  + 
								", Reason: result code present in answer packet is not " +
								"configured in priority result code field.");
					}
					return;
				}
			}
		}
		
		for (IDiameterAVP avp : translatedAnswer.getAVPList()) {
			String avpId = avp.getAVPId();

			if (DiameterAVPConstants.SESSION_ID.equals(avpId)) {
				continue;
			}
			if (DiameterAVPConstants.RESULT_CODE.equals(avpId)) {
				updateResultCode(originalResponse, avp);
			} else if (DiameterAVPConstants.ORIGIN_HOST.equals(avpId)
					|| DiameterAVPConstants.ORIGIN_REALM.equals(avpId)) {
				DiameterUtility.addOrReplaceAvp(avpId, originalResponse.getDiameterAnswer(), avp.getStringValue());
			} else {
				originalResponse.addAVP(avp);
			}
		}
		
		if (ResultCodeCategory.getResultCodeCategory(originalResponse.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger()).isFailureCategory) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Skipping further processing, Reason: Failure result code " + 
						ResultCode.fromCode((int)originalResponse.getAVP(
						DiameterAVPConstants.RESULT_CODE).getInteger()) + " in packet.");
			}
			originalResponse.setFurtherProcessingRequired(false);
			originalResponse.setProcessingCompleted(true);
		}
	}
	
	private void updateResultCode(ApplicationResponse originalResponse, IDiameterAVP remoteResultCodeAVP) {
		int resultCode = (int) remoteResultCodeAVP.getInteger();

		if (originalResponse.getDiameterAnswer().getParameter(DiameterAVPConstants.RESULT_CODE) != null) {
			int existingResultCode = (Integer) originalResponse.getDiameterAnswer().getParameter(DiameterAVPConstants.RESULT_CODE);
			resultCode = data.getPrioritizedResultCode(existingResultCode, resultCode);
		}

		originalResponse.getDiameterAnswer().setParameter(DiameterAVPConstants.RESULT_CODE, resultCode);

		/**
		   * Following if-else block is to maintain the
		   * highest priority result code
		   * among the result codes of all the positive
		   * and negative responses received. This parameter  
		   * is updated here as well as from outside in case
		   * when a received response is negative.
		   */ 
		   
		if (originalResponse.getParameter(HIGHEST_PRIORITY_RESULT_CODE) == null) {
			originalResponse.getDiameterAnswer().setParameter(HIGHEST_PRIORITY_RESULT_CODE, resultCode);
		} else {
			int highestPriorityResultCode = data.getPrioritizedResultCode(resultCode, (Integer)originalResponse.getParameter(HIGHEST_PRIORITY_RESULT_CODE));
			originalResponse.getDiameterAnswer().setParameter(HIGHEST_PRIORITY_RESULT_CODE, highestPriorityResultCode);
		}
		
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
				originalResponse.getDiameterAnswer(), resultCode+"");

		if (ResultCodeCategory.getResultCodeCategory(resultCode) == ResultCodeCategory.RC3XXX) {
			originalResponse.getDiameterAnswer().setErrorBit();
		} else {
			originalResponse.getDiameterAnswer().resetErrorBit();
		}
		
	}

	public class TranslatingBroadcastResponseListener implements ResponseListener {

		private int orderId;
		private final DiameterBroadcastResponseListener listener;
		private final ApplicationResponse originalResponse;
		private final ApplicationRequest originalRequest;
		private final TranslationHelper translationHelper;
		private final DiameterRequest remoteRequest;

		public TranslatingBroadcastResponseListener(DiameterBroadcastResponseListener listener,
				ApplicationRequest request,
				ApplicationResponse response,
				TranslationHelper translationHelper,
				DiameterRequest remoteRequest, int orderId) {
			this.listener = listener;
			this.translationHelper = translationHelper;
			this.remoteRequest = remoteRequest;
			this.orderId = orderId;
			this.originalResponse = response;
			this.originalRequest = request;
		}

		@Override
		public void responseReceived(DiameterAnswer remoteAnswer, String hostIdentity, DiameterSession diameterSession) {

			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Diameter answer received with Session-Id=" 
						+ remoteAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}

			listener.addAsyncRequestExecutor(
					DiameterOrderedAsyncRequestExecutor.from(
							new TranslationExecutor(originalRequest, originalResponse, DiameterUtility.newAnswerWithHeaderOf(remoteRequest), 
									remoteAnswer, translationHelper),
							orderId));

			listener.responseReceived(remoteRequest, remoteAnswer, diameterSession);
		}

		@Override
		public void requestTimedout(String hostIdentity, DiameterSession diameterSession) {

			DiameterAnswer remoteDiameterAnswer;
			if (data.isAcceptableResultCode(ResultCode.DIAMETER_REQUEST_TIMEOUT.code) == false) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Timeout occured for request with Session-Id=" 
							+ remoteRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) 
							+ ", sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER 
							+ ", as accept on timeout is disabled.");
				}
				remoteDiameterAnswer = new DiameterAnswer(remoteRequest, ResultCode.DIAMETER_UNABLE_TO_DELIVER);
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, remoteDiameterAnswer, DiameterErrorMessageConstants.REQUEST_TIMEOUT);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Timeout occured for request with Session-Id=" 
							+ remoteRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) 
							+ ", sending " + ResultCode.DIAMETER_SUCCESS 
							+ ", as accept on timeout is enabled.");
				}
				remoteDiameterAnswer = new DiameterAnswer(remoteRequest, ResultCode.DIAMETER_SUCCESS);
			}

			responseReceived(remoteDiameterAnswer, hostIdentity, diameterSession);
		}
	}

	private class TranslationExecutor implements DiameterAsyncRequestExecutor {
		private DiameterAnswer remoteAnswer;
		private ApplicationRequest originalRequest;
		private ApplicationResponse originalResponse;
		private TranslationHelper translationHelper;
		private DiameterAnswer translatedAnswer;

		public TranslationExecutor(ApplicationRequest originalRequest,
				ApplicationResponse originalResponse,
				DiameterAnswer translatedAnswer,
				DiameterAnswer remoteAnswer,
				TranslationHelper translationHelper) {
			this.originalRequest = originalRequest;
			this.originalResponse = originalResponse;
			this.remoteAnswer = remoteAnswer;
			this.translationHelper = translationHelper;
			this.translatedAnswer = translatedAnswer;
		}

		@Override
		public void handleServiceRequest(ApplicationRequest serviceRequest, ApplicationResponse serviceResponse,
				ISession session) {
			translateAnswer(session);
			
			if (translatedAnswer.isFailureCategory() == false) {
				aggregate(originalResponse,	translatedAnswer);
			} else {
				if (data.isAcceptableResultCode((int)translatedAnswer.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger())){
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE,"Ignoring answer packet received from " +
						translatedAnswer.getAnsweringHost() + ", Reason: " + 
						"result code configured in answer packet is " +
						"configured in acceptable result code field.");
					}
					return;
				}
				
				aggregateNegativeAnswer();
			}
		}

		private void aggregateNegativeAnswer() {
			if(LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Aggregating answer received from : " + translatedAnswer.getAnsweringHost());
			}
			
			if (data.getPriorityResultCodes().isEmpty()) {
				setTranslatedAnswerAsOriginalResponse(originalResponse,translatedAnswer);
				originalResponse.setFurtherProcessingRequired(false);
				originalResponse.setProcessingCompleted(true);
			} else {
				int translatedAnswerResultCode = (int) translatedAnswer.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger();
				if (data.getPriorityResultCodes().contains(translatedAnswerResultCode) ||
						data.getPriorityResultCodes().contains(ResultCodeCategory.getResultCodeCategory(translatedAnswerResultCode).value)) {

					int priorityResultCode;

					if (originalResponse.getParameter(HIGHEST_PRIORITY_RESULT_CODE) == null) {
						priorityResultCode = translatedAnswerResultCode;
					} else {
						priorityResultCode = data.getPrioritizedResultCode((Integer)originalResponse.getParameter(HIGHEST_PRIORITY_RESULT_CODE), translatedAnswerResultCode);
					}

					if(priorityResultCode == translatedAnswerResultCode) {
						originalResponse.setParameter(HIGHEST_PRIORITY_RESULT_CODE, priorityResultCode);
						originalResponse.setParameter(HIGHEST_PRIORITY_NEGATIVE_ANSWER, translatedAnswer);
					}
				} else {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE,"Ignoring answer packet received from " +
								translatedAnswer.getAnsweringHost()  + 
								", Reason: result code present in answer packet is not " +
								"configured in priority result code field.");
					}
				}
			}
		}

		private void translateAnswer(ISession session) {

			try {
				translatedAnswer = translationHelper.translateAnswer(remoteAnswer, translatedAnswer, session);
			} catch (TranslationFailedException e) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Failed to translate Response using mapping: "
							+ data.getTranslationMapping() + ", Reason: " + e.getMessage());
				}
				LogManager.getLogger().trace(MODULE, e);

				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
						translatedAnswer, ResultCode.DIAMETER_UNABLE_TO_COMPLY.code+ "");
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
						translatedAnswer, DiameterErrorMessageConstants.translationFailed(data.getTranslationMapping()));
			}
			if (translatedAnswer.isFailureCategory() == false) {
				aggregate(originalResponse,	translatedAnswer);
			}
		}
		
		private void setTranslatedAnswerAsOriginalResponse(ApplicationResponse originalResponse,
				DiameterAnswer translatedAnswer) {
			translatedAnswer.setHeaderFrom(originalRequest.getDiameterRequest());
			originalResponse.getDiameterAnswer().setBytes(translatedAnswer.getBytes());
			if (ResultCodeCategory.getResultCodeCategory(translatedAnswer.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger()) == ResultCodeCategory.RC3XXX 
					|| translatedAnswer.isError()) {
				originalResponse.getDiameterAnswer().setErrorBit();
			}
		}

	}

	private class TranslationHelper {
		private final DiameterRequest originalRequest;
		private DiameterRequest remoteRequest;
		private String selectedRequestMapping;
		private boolean dummyMappingApplicable;
		private DiameterAnswer originalAnswer;

		public TranslationHelper(DiameterRequest originalRequest,
				DiameterAnswer originalAnswer) {
			this.originalRequest = originalRequest;
			this.originalAnswer = originalAnswer;
		}
		
		public void onTranslationFailure(DiameterAnswer answer) {
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
					answer, ResultCode.DIAMETER_UNABLE_TO_COMPLY.code+ "");
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
					answer, DiameterErrorMessageConstants.translationFailed(data.getTranslationMapping()));
		}

		public DiameterAnswer getDummyAnswer() {
			DiameterAnswer answer = DiameterUtility.newAnswerWithHeaderOf(remoteRequest);
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.SESSION_ID, answer, remoteRequest.getSessionID());	
			answer.addAvps(formDummyAnswerAVPs(data.getTranslationMapping()));
			return answer;
		}

		DiameterRequest translateRequest(DiameterSession requestorSession) throws TranslationFailedException {

			if (Strings.isNullOrBlank(data.getTranslationMapping())) {
				try {
					remoteRequest = (DiameterRequest) originalRequest.clone();
					remoteRequest.touch();
					remoteRequest.setHop_by_hopIdentifier(HopByHopPool.get());
				} catch (CloneNotSupportedException e) {
					throw new AssertionError(e);
				}
				return remoteRequest;
			}

			remoteRequest = new DiameterRequest();
			remoteRequest.setCommandCode(originalRequest.getCommandCode());
			remoteRequest.setApplicationID(originalRequest.getApplicationID());
			remoteRequest.setEnd_to_endIdentifier(originalRequest.getEnd_to_endIdentifier());

			if (originalRequest.isProxiable()){
				remoteRequest.setProxiableBit();
			}
			if (originalRequest.isError()){
				remoteRequest.setErrorBit();
			}
			if (originalRequest.isReTransmitted()){
				remoteRequest.setReTransmittedBit();
			}

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Translating request using translation policy: " 
						+ data.getTranslationMapping());
			}

			TranslatorParams translatorParams = new TranslatorParamsImpl(originalRequest, remoteRequest);
			translatorParams.setParam(TranslatorConstants.DIAMETER_SESSION, requestorSession);
			translatorParams.setParam(TranslatorConstants.SRCRES, originalAnswer);
			translationAgent.translate(data.getTranslationMapping(), translatorParams, TranslatorConstants.REQUEST_TRANSLATION);
			remoteRequest = (DiameterRequest) translatorParams.getParam(TranslatorConstants.TO_PACKET);
			remoteRequest.setHop_by_hopIdentifier(HopByHopPool.get());

			dummyMappingApplicable = Boolean.parseBoolean(String.valueOf(translatorParams.getParam(TranslatorConstants.DUMMY_MAPPING)));
			selectedRequestMapping = (String)translatorParams.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);
			return remoteRequest;
		}

		public boolean isDummyMappingApplicable() {
			return dummyMappingApplicable;
		}

		private DiameterAnswer translateAnswer(DiameterAnswer remoteAnswer, DiameterAnswer translatedAnswer,
				ISession session) throws TranslationFailedException {

			translatedAnswer = translateAnswerInternal(remoteAnswer, translatedAnswer, session);

			IDiameterAVP resultCodeAVP = translatedAnswer.getAVP(DiameterAVPConstants.RESULT_CODE);
			if(resultCodeAVP == null) {
				LogManager.getLogger().warn(MODULE, "Result-code AVP not found in translated remote response, using " + ResultCode.DIAMETER_SUCCESS.code + " as default value");
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, translatedAnswer, String.valueOf(ResultCode.DIAMETER_SUCCESS.code));
			}

			return translatedAnswer;
		}

		private DiameterAnswer translateAnswerInternal(DiameterAnswer remoteAnswer, DiameterAnswer translatedAnswer,
				ISession requestorSession) throws TranslationFailedException {
			if (Strings.isNullOrBlank(data.getTranslationMapping())) {
				return remoteAnswer;
			}

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Translating answer using translation policy: " 
						+ data.getTranslationMapping());
			}

			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.SESSION_ID, translatedAnswer, remoteRequest.getSessionID());

			if (remoteAnswer.isProxiable()){
				translatedAnswer.setProxiableBit();
			}
			if (remoteAnswer.isError()){
				translatedAnswer.setErrorBit();
			}
			if (remoteAnswer.isReTransmitted()){
				translatedAnswer.setReTransmittedBit();
			}

			TranslatorParams translatorParam = new TranslatorParamsImpl(remoteAnswer, translatedAnswer, 
					originalRequest, remoteRequest);
			translatorParam.setParam(TranslatorConstants.DIAMETER_SESSION, requestorSession);
			translatorParam.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING,
					selectedRequestMapping);
			translatorParam.setParam(TranslatorConstants.SRCRES, originalAnswer);

			translationAgent.translate(data.getTranslationMapping(), translatorParam, 
					TranslatorConstants.RESPONSE_TRANSLATION);
			translatedAnswer = (DiameterAnswer) translatorParam.getParam(TranslatorConstants.TO_PACKET);
			return translatedAnswer;
		}

		private List<IDiameterAVP> formDummyAnswerAVPs(String transMapName) {

			Map<String,String> dummyMappings = translationAgent.getDummyResponseMap(transMapName);
			if (Maps.isNullOrEmpty(dummyMappings)) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "No Dummy Mappings defined for translation policy: " + transMapName);
				}
				return null;
			}
			List<IDiameterAVP> dummyAVPs = new ArrayList<IDiameterAVP>();
			for (Entry<String,String> mapping : dummyMappings.entrySet()) {
				dummyAVPs.add(DiameterUtility.createAvp(mapping.getKey(), mapping.getValue()));
			}
			return dummyAVPs;
		}
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return group.isAlive() == false;
	}
	
	private void addRouteRecoredAVP(DiameterRequest originalRequest, DiameterRequest destinationRequest) {

		String requesterHostId = originalRequest.getRequestingHost();
		if (Strings.isNullOrBlank(requesterHostId) == false  && (requesterHostId.equalsIgnoreCase(Parameter.getInstance().getOwnDiameterIdentity()) == false)) {
			IDiameterAVP routeRecord = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ROUTE_RECORD);
			routeRecord.setStringValue(requesterHostId);
			destinationRequest.addAvp(routeRecord);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Added Route-Record-AVP " + DiameterAVPConstants.ROUTE_RECORD + " with value: "+ requesterHostId + " in remote request.");
			}
		}
	}
}