package com.elitecore.aaa.diameter.service.application.handlers;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.impl.RadEsiGroupData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterExternalCommunicationEntryData;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommGroup;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommGroupImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.peer.group.DefaultDiameterPeerGroup;
import com.elitecore.diameterapi.core.common.transport.VirtualConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.EndToEndPool;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;

/**
 * TODO log on translation failure
 * 
 * TODO Partially Done - result code + error bit combination, when acceptable result code is found then we don't have to
 * consider the error bit of remote answer. Have to verify what to do when error code category is such that it depends on
 * the error code whether error bit is to be set or not.
 *  
 * FIXME what do do in case wer aggregate() is not called
 *  
 * @author monica.lulla
 * @author narendra.pathai
 */
public class DiaToRadProxyCommunicationHandler implements DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> {

	protected static final String MODULE = "DIA-TO-RAD-PRXY-HNDLR";
	private static final String BROADCAST_LISTENER = "BROADCAST_LISTENER";
	private static final String TRANSLATION_HELPER = "TRANSLATION_MAPPING";
	private static final String RADIUS_LISTENER_INTERNAL = "RADIUS_BROADCAST_LISTENER";
	private static final String REMOTE_RADIUS_REQUEST = "REMOTE_RADIUS_REQUEST";
	private static final String REMOTE_RADIUS_RESPONSE = "REMOTE_RADIUS_RESPONSE";

	private DiameterExternalCommunicationEntryData data;
	private DiameterServiceContext serviceContext;
	private ITranslationAgent translationAgent;
	private DefaultDiameterPeerGroup group;

	public DiaToRadProxyCommunicationHandler(DiameterExternalCommunicationEntryData entry,
			DiameterServiceContext serviceContext, ITranslationAgent translationAgent) {
		this.data = entry;
		this.serviceContext = serviceContext;
		this.translationAgent = translationAgent;
		this.group = new DefaultDiameterPeerGroup(serviceContext.getStackContext(), 
				data.getPeerGroupName(), 0, 3000, false);
	}

	@Override
	public void init() throws InitializationFailedException {

		if (Strings.isNullOrBlank(data.getTranslationMapping())) {
			throw new InitializationFailedException("Translation Mapping is not configured.");
		}
		
		if (translationAgent.isExists(data.getTranslationMapping()) == false) {
			throw new InitializationFailedException("Translation Mapping: " + data.getTranslationMapping() 
				+ " is not registered.");
		}
		
		RadEsiGroupData groupData = ((AAAServerContext)this.serviceContext.getServerContext()).getServerConfiguration().getRadEsiGroupConfigurable().getRadEsiGroup(data.getPeerGroupId());

		for (PeerInfoImpl entry : groupData.getEsis()) {
			registerVirtualRoutingPeer(entry);
		}
	}

	/**
	 * Registers a virtual Routing peer to the DiameterStack
	 * @param entry 
	 * @throws InitializationFailedException 
	 */
	private void registerVirtualRoutingPeer(final PeerInfoImpl entry) throws InitializationFailedException {
		
		Optional<DefaultExternalSystemData> optionalESI = serviceContext.getServerContext().getServerConfiguration()
				.getRadESConfiguration().getESDataByName(entry.getPeerName());
		
		if (optionalESI.isPresent() == false) {
			throw new InitializationFailedException("Radius ESI: " + entry.getPeerName() + 
					" configuration not found.");
		}

		if (serviceContext.getStackContext().getPeerData(entry.getPeerName()) == null) {
			registerVirtualPeer(entry, optionalESI);
		} else {
			LogManager.getLogger().debug(MODULE, "Virtual Diameter Peer: " + entry.getPeerName() + 
					" is already registered.");
		}
		group.addCommunicator(serviceContext.getStackContext().getPeerCommunicator(entry.getPeerName()), entry.getLoadFactor());
	}

	private void registerVirtualPeer(final PeerInfoImpl entry, Optional<DefaultExternalSystemData> optionalESI)
			throws InitializationFailedException {
		final PeerDataImpl peerData = new PeerDataImpl();
		peerData.setPeerName(entry.getPeerName());
		peerData.setHostIdentity(entry.getPeerName());
		peerData.setRealmName(Parameter.getInstance().getOwnDiameterRealm());
		peerData.setInitiateConnectionDuration(0);
		peerData.setWatchdogInterval(0);

		DefaultExternalSystemData radEsi = optionalESI.get();

		peerData.setPeerTimeout(radEsi.getCommunicationTimeout() * radEsi.getRetryLimit() + 1000);
		peerData.setRequestTimeout(radEsi.getCommunicationTimeout());
		peerData.setRetransmissionCount(radEsi.getRetryLimit());
		peerData.setRemoteIPAddress(radEsi.getIPAddress().getHostAddress());

		UDPCommunicator udpCommunicator = serviceContext.getServerContext().getRadUDPCommunicatorManager().findCommunicatorByIDOrCreate(radEsi.getUUID(), serviceContext.getServerContext(), radEsi);
		try {
			RadiusVirtualOutputStream radiusVirtualOutputStream = new RadiusVirtualOutputStream(serviceContext, radEsi, udpCommunicator);
			VirtualConnectionHandler virtualConnectionHandler = serviceContext.getStackContext().registerVirtualPeer(peerData, radiusVirtualOutputStream);
			radiusVirtualOutputStream.setConnectionHandler(virtualConnectionHandler);
			udpCommunicator.addESIEventListener(new RadESIEventListener(peerData, virtualConnectionHandler));
		} catch (ElementRegistrationFailedException e) {
			throw new InitializationFailedException(e);
		}
	}

	static class RadESIEventListener implements ESIEventListener<ESCommunicator> {
		private DiameterRequest dpr = new DiameterRequest();
		private DiameterRequest cer = new DiameterRequest();
		private VirtualConnectionHandler virtualConnectionHandler;

		public RadESIEventListener(PeerDataImpl peerData, 
				VirtualConnectionHandler virtualConnectionHandler) {
			this.virtualConnectionHandler = virtualConnectionHandler;
			dpr.setCommandCode(CommandCode.DISCONNECT_PEER.code);
			dpr.setApplicationID(ApplicationIdentifier.BASE.applicationId);
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DISCONNECT_CAUSE, dpr, DiameterAttributeValueConstants.REBOOTING + "");
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ORIGIN_HOST, dpr, peerData.getPeerName());
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ORIGIN_REALM, dpr, peerData.getRealmName());

			cer.setCommandCode(CommandCode.CAPABILITIES_EXCHANGE.code);
			cer.setApplicationID(ApplicationIdentifier.BASE.applicationId);
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.AUTH_APPLICATION_ID, cer, ApplicationIdentifier.RELAY.applicationId + "");
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ORIGIN_HOST, cer, peerData.getPeerName());
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ORIGIN_HOST, cer, peerData.getRealmName());
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.PRODUCT_NAME, cer, Parameter.getInstance().getProductName());
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.VENDOR_ID, cer, Parameter.getInstance().getVendorId() + "");
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ORIGIN_STATE_ID, cer, Parameter.getInstance().getOriginStateId() + "");
		}

		@Override
		public void alive(ESCommunicator esCommunicator) {
			cer.setHop_by_hopIdentifier(HopByHopPool.get());
			cer.setEnd_to_endIdentifier(EndToEndPool.get());
			virtualConnectionHandler.getInputStream().received(cer);
		}

		@Override
		public void dead(ESCommunicator esCommunicator) {
			dpr.setHop_by_hopIdentifier(HopByHopPool.get());
			dpr.setEnd_to_endIdentifier(EndToEndPool.get());
			virtualConnectionHandler.getInputStream().received(dpr);
		}

	}

	static class RadiusVirtualOutputStream implements VirtualOutputStream {

		private RadUDPCommGroup radiusCommGroup;
		private DefaultExternalSystemData radEsiData;
		private VirtualConnectionHandler virtualConnectionHandler;

		public RadiusVirtualOutputStream(DiameterServiceContext serviceContext, 
				DefaultExternalSystemData radEsiData, 
				UDPCommunicator udpCommunicator) {
			this.radEsiData = radEsiData;
			radiusCommGroup = new RadUDPCommGroupImpl(serviceContext.getServerContext());
			radiusCommGroup.addCommunicator(udpCommunicator);
		}

		public void setConnectionHandler(VirtualConnectionHandler virtualConnectionHandler) {
			this.virtualConnectionHandler = virtualConnectionHandler;
		}

		/**
		 *  Diameter Packet to be sent to Radius ESI will arrive here.
		 *  
		 *  This will translate Diameter Packet to Radius Packet and will send to Radius ESI
		 *  
		 */
		@Override
		public void send(Packet remotePacket) {

			DiameterPacket remoteDiameterPacket = (DiameterPacket) remotePacket;

			DiameterBroadcastResponseListener listener = (DiameterBroadcastResponseListener) remoteDiameterPacket.getParameter(BROADCAST_LISTENER);
			if (remoteDiameterPacket.isRequest()) {
				
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Request with Session-ID=" + remoteDiameterPacket.getSessionID() 
						+ " received by Peer: " + radEsiData.getName());
				}
				
				if (listener == null) {
					handleDiameterRequest(remoteDiameterPacket.getAsDiameterRequest());
				} else {
					handleAsyncDiameterRequest(remoteDiameterPacket.getAsDiameterRequest(), listener);
				}
			} else {
				handleDiameterAnswer(remoteDiameterPacket.getAsDiameterAnswer());
			}

		}

		private void handleDiameterAnswer(DiameterAnswer remoteDiameterAnswer) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Answer received by Peer: " + 
						radEsiData.getName() + remoteDiameterAnswer);
			}

		}

		private void handleDiameterRequest(DiameterRequest remoteRequest) {
			
			RadiusProxyResponseListener radiusResponseListener = 
					(RadiusProxyResponseListener) remoteRequest.getParameter(RADIUS_LISTENER_INTERNAL);

			radiusResponseListener.setCurrentVirtualConnectionHandler(virtualConnectionHandler);

			RadiusPacket remoteRadiusPacket = (RadiusPacket) remoteRequest.getParameter(REMOTE_RADIUS_REQUEST);
			radiusCommGroup.handleRequest(remoteRadiusPacket.getBytes(), AAAServerConstants.DIAMETER_TO_RADIUS_SECRET,
					radiusResponseListener, HazelcastRadiusSession.RAD_NO_SESSION);
		}

		private void handleAsyncDiameterRequest(DiameterRequest remoteRequest, 
				DiameterBroadcastResponseListener listener) {

			RadiusPacket translatedRequest = (RadiusPacket) remoteRequest.getParameter(REMOTE_RADIUS_REQUEST);
			
			RadiusBroadcastResponseListenerInternal radiusBroadcastResponseListener = (RadiusBroadcastResponseListenerInternal) remoteRequest.getParameter(RADIUS_LISTENER_INTERNAL);
			radiusBroadcastResponseListener.setVirtualConnectionHandler(virtualConnectionHandler);

			radiusCommGroup.handleRequest(translatedRequest.getBytes(), AAAServerConstants.DIAMETER_TO_RADIUS_SECRET,
					radiusBroadcastResponseListener, HazelcastRadiusSession.RAD_NO_SESSION);
		}
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}

	@Override
	public void handleRequest(final ApplicationRequest request, final ApplicationResponse response, ISession session) {

		final DiameterSession diameterSession = (DiameterSession) session;
		final DiameterRequest remoteDiameterRequest;
		try {
			remoteDiameterRequest = (DiameterRequest) request.getDiameterRequest().clone();
			remoteDiameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
			remoteDiameterRequest.touch();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}

		DiaToRadTranslationHelper diaToRadTranslationHelper = new DiaToRadTranslationHelper(remoteDiameterRequest);

		RadiusPacket remoteRadiusRequest;
		try {
			remoteRadiusRequest = diaToRadTranslationHelper.translateRequest();
		} catch (TranslationFailedException e) {
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_UNABLE_TO_COMPLY, 
					DiameterErrorMessageConstants.translationFailed(data.getTranslationMapping()));
			return;
		}

		if (diaToRadTranslationHelper.isDummyMappingApplicable()) {

			DiameterAnswer remoteDiameterAnswer;
			try {
				remoteDiameterAnswer = diaToRadTranslationHelper.translateAnswer(null,
						DiameterUtility.newAnswerWithHeaderOf(remoteDiameterRequest));

				aggregate(response, remoteDiameterAnswer);

			} catch (TranslationFailedException e) {
				DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_UNABLE_TO_COMPLY, 
						DiameterErrorMessageConstants.translationFailed(data.getTranslationMapping()));
			}
			return;
		}

		final RadiusProxyResponseListener radiusProxyResponseListener = new RadiusProxyResponseListener(request, response, 
				remoteDiameterRequest, diaToRadTranslationHelper);
		ResponseListener diameterProxyResponseListener = new ResponseListener() {
			
			@Override
			public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession diameterSession) {
				serviceContext.resumeRequestInAsync(diameterSession, request, response, 
						radiusProxyResponseListener.new TranslationExecutor());
			}
			
			@Override
			public void requestTimedout(String hostIdentity, DiameterSession diameterSession) {
				if (data.isAcceptableResultCode(ResultCode.DIAMETER_REQUEST_TIMEOUT.code) == false) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Timeout occured for request with Session-Id=" 
								+ remoteDiameterRequest.getSessionID() 
								+ ", sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER 
								+ ", as accept on timeout is disabled.");
					}

					DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
							response.getDiameterAnswer(), ResultCode.DIAMETER_UNABLE_TO_DELIVER.code+ "");
					DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
							response.getDiameterAnswer(), DiameterErrorMessageConstants.REQUEST_TIMEOUT);

				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Timeout occured for request with Session-Id=" 
								+ remoteDiameterRequest.getSessionID() 
								+ ", sending " + ResultCode.DIAMETER_SUCCESS 
								+ ", as accept on timeout is enabled.");
						DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
								response.getDiameterAnswer(), ResultCode.DIAMETER_SUCCESS.code+ "");
					}						
				}
				serviceContext.resumeRequestInAsync(diameterSession, request, response);
			}
		};
		
		remoteDiameterRequest.setParameter(REMOTE_RADIUS_REQUEST, remoteRadiusRequest);
		remoteDiameterRequest.setParameter(TRANSLATION_HELPER, diaToRadTranslationHelper);
		remoteDiameterRequest.setParameter(RADIUS_LISTENER_INTERNAL, 
				radiusProxyResponseListener);

		try {
			DiameterProcessHelper.onExternalCommunication(request, response);
				group.sendClientInitiatedRequest(diameterSession, remoteDiameterRequest, diameterProxyResponseListener);
		} catch (CommunicationException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER 
						+ ", Reason: " + e.getMessage());
			}
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_UNABLE_TO_DELIVER, 
					DiameterErrorMessageConstants.DEST_PEER_NOT_FOUND);
		}

	}

	@Override
	public void handleAsyncRequest(final ApplicationRequest request, final ApplicationResponse response,
			final ISession session, final DiameterBroadcastResponseListener listener) {

		final DiameterSession diameterSession = (DiameterSession) session;
		final DiameterRequest remoteDiameterRequest;
		try {
			remoteDiameterRequest = (DiameterRequest) request.getDiameterRequest().clone();
			remoteDiameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
			remoteDiameterRequest.touch();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}

		final int order = listener.getNextOrder();
		DiaToRadTranslationHelper diaToRadTranslationHelper = new DiaToRadTranslationHelper(remoteDiameterRequest);
		
		RadiusPacket remoteRadiusRequest;
		try {
			remoteRadiusRequest = diaToRadTranslationHelper.translateRequest();
		} catch (TranslationFailedException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_COMPLY 
						+ ", Reason: " + e.getMessage());
			}
 
			final DiameterAnswer remoteDiameterAnswer = new DiameterAnswer(remoteDiameterRequest, 
					ResultCode.DIAMETER_UNABLE_TO_COMPLY);
			listener.addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor.from(
					new DiameterAsyncRequestExecutor() {
						
						@Override
						public void handleServiceRequest(ApplicationRequest serviceRequest, ApplicationResponse serviceResponse,
								ISession session) {
							aggregate(serviceResponse, remoteDiameterAnswer);
							
						}
					}, order));
			
			listener.responseReceived(remoteDiameterRequest, remoteDiameterAnswer, session);
			return;
		}

		if (diaToRadTranslationHelper.isDummyMappingApplicable()) {

			DiameterAnswer remoteDiameterAnswer = DiameterUtility.newAnswerWithHeaderOf(remoteDiameterRequest);
			
			listener.addAsyncRequestExecutor(
					DiameterOrderedAsyncRequestExecutor.from(
							new BroadcastTranslationExecutor(null, 
									remoteDiameterAnswer, 
									response, diaToRadTranslationHelper),
							listener.getNextOrder()));

			listener.responseReceived(remoteDiameterRequest, remoteDiameterAnswer, session);

			return;
		}

		RadiusBroadcastResponseListenerInternal broadcastResponseListenerInternal = new RadiusBroadcastResponseListenerInternal(request,
				response, remoteDiameterRequest, listener,	diaToRadTranslationHelper, order);
		
		ResponseListener responseListener = new ResponseListener() {
			
			@Override
			public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession diameterSession) {
				listener.responseReceived(remoteDiameterRequest, diameterAnswer, session);
			}
			
			@Override
			public void requestTimedout(String hostIdentity, DiameterSession diameterSession) {
				// Auto-generated method stub
				
			}
		};

		remoteDiameterRequest.setParameter(REMOTE_RADIUS_REQUEST, remoteRadiusRequest);
		remoteDiameterRequest.setParameter(TRANSLATION_HELPER, diaToRadTranslationHelper);
		remoteDiameterRequest.setParameter(BROADCAST_LISTENER, listener);

		remoteDiameterRequest.setParameter(RADIUS_LISTENER_INTERNAL, 
				broadcastResponseListenerInternal);

		try {
			group.sendClientInitiatedRequest(diameterSession, remoteDiameterRequest, responseListener);
		} catch (CommunicationException e) {

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER 
						+ ", Reason: " + e.getMessage());
			}
 
			final DiameterAnswer remoteDiameterAnswer = new DiameterAnswer(remoteDiameterRequest, ResultCode.DIAMETER_UNABLE_TO_DELIVER);
			listener.addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor.from(
					new DiameterAsyncRequestExecutor() {

						@Override
						public void handleServiceRequest(ApplicationRequest serviceRequest,
								ApplicationResponse serviceResponse, ISession session) {
							aggregate(serviceResponse, remoteDiameterAnswer);
						}					
					}, order));
			
			listener.responseReceived(remoteDiameterRequest, remoteDiameterAnswer, session);
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}


	class RadiusProxyResponseListener implements RadResponseListener {

		private ApplicationResponse originalResponse;
		private DiameterRequest remoteRequest;
		private DiaToRadTranslationHelper translationHelper;

		/*
		 * This entity is injected by VOutputStream when a peer communication occurs and can
		 * be injected multiple times when failover occurs. So this will present the current
		 * handler through which the communication occurred.
		 */
		private VirtualConnectionHandler currentVirtualConnectionHandler;
		private DiameterAnswer remoteDiameterAnswer;
		private ApplicationRequest originalRequest;

		public RadiusProxyResponseListener(ApplicationRequest originalRequest, 
				ApplicationResponse originalResponse, 
				DiameterRequest remoteRequest, 
				DiaToRadTranslationHelper translationHelper) {
			this.originalRequest = originalRequest;
			this.originalResponse = originalResponse;
			this.remoteRequest = remoteRequest;
			this.translationHelper = translationHelper;
			this.remoteDiameterAnswer = DiameterUtility.newAnswerWithHeaderOf(remoteRequest);
		}

		public void setCurrentVirtualConnectionHandler(VirtualConnectionHandler currentVirtualConnectionHandler) {
			this.currentVirtualConnectionHandler = currentVirtualConnectionHandler;
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest, final RadUDPResponse radUDPResponse, ISession session) {
			// This will be called from the context of radius response listener thread. To improve performance
			// the task of translation from radius to diameter is offloaded to diameter threads which is being
			// passed as an executor to resumeInAsync
			remoteRequest.setParameter(REMOTE_RADIUS_RESPONSE, radUDPResponse);

			/** set shared secret of RHS side AAA */
			remoteRequest.setParameter(TranslatorConstants.SHARED_SECRET, radUDPRequest.getSharedSecret());
			sendToVirtualStream(currentVirtualConnectionHandler, originalRequest.getDiameterRequest().getSessionID(), remoteDiameterAnswer);
		}

		@Override
		//FIXME dont yet know what to do :(:(:(
		public void requestDropped(RadUDPRequest radUDPRequest) {

			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
					remoteDiameterAnswer, ResultCode.DIAMETER_UNABLE_TO_DELIVER.code+ "");
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
					remoteDiameterAnswer, DiameterErrorMessageConstants.DEST_PEER_NOT_FOUND);
			
//			sendToVirtualStream(currentVirtualConnectionHandler, requestorSession, remoteDiameterAnswer);
		}

		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
//			sendToVirtualStream(currentVirtualConnectionHandler, requestorSession, remoteDiameterAnswer);
		}

		class TranslationExecutor implements DiameterAsyncRequestExecutor {

			@Override
			public void handleServiceRequest(ApplicationRequest serviceRequest, ApplicationResponse serviceResponse,
					ISession session) {
				RadUDPResponse remoteUdpResponse = (RadUDPResponse) remoteRequest.getParameter(REMOTE_RADIUS_RESPONSE);
				RadiusPacket remoteResponsePacket = (RadiusPacket) remoteUdpResponse.getRadiusPacket();

				try {
					remoteDiameterAnswer = translationHelper.translateAnswer(remoteResponsePacket, remoteDiameterAnswer);
				} catch (TranslationFailedException e) {
					DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
							remoteDiameterAnswer, ResultCode.DIAMETER_UNABLE_TO_COMPLY.code+ "");
					DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
							remoteDiameterAnswer, DiameterErrorMessageConstants.translationFailed(data.getTranslationMapping()));
				}
				aggregate(originalResponse, remoteDiameterAnswer);
			}
		}
	}

	private void aggregate(ApplicationResponse originalResponse, DiameterAnswer translatedAnswer) {

		for (IDiameterAVP avp : translatedAnswer.getAVPList()) {
			if (DiameterAVPConstants.ORIGIN_HOST.equals(avp.getAVPId())
					|| DiameterAVPConstants.ORIGIN_REALM.equals(avp.getAVPId())
					|| DiameterAVPConstants.SESSION_ID.equals(avp.getAVPId())) {
				continue;
			}

			if (DiameterAVPConstants.RESULT_CODE.equals(avp.getAVPId())) {
				updateResultCode(originalResponse, avp);
			} else {
				originalResponse.addAVP(avp);
			}
		}
	}

	private void updateResultCode(ApplicationResponse originalResponse, IDiameterAVP avp) {
		int resultCode = (int) avp.getInteger();

		if (data.isAcceptableResultCode(resultCode)) {
			resultCode = ResultCode.DIAMETER_SUCCESS.code;
		}

		int existingResultCode = 0;
		if (originalResponse.getAVP(DiameterAVPConstants.RESULT_CODE) != null) {
			existingResultCode = (int) originalResponse.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger();
		}
		resultCode = data.getPrioritizedResultCode(existingResultCode, resultCode);

		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
				originalResponse.getDiameterAnswer(), resultCode+"");
		
		if (ResultCodeCategory.getResultCodeCategory(resultCode) == ResultCodeCategory.RC3XXX
				|| ResultCodeCategory.getResultCodeCategory(resultCode) == ResultCodeCategory.RC5XXX) {
			originalResponse.getDiameterAnswer().setErrorBit();
		} else {
			originalResponse.getDiameterAnswer().resetErrorBit();
		}
	}
	
	private void sendToVirtualStream(VirtualConnectionHandler currentVirtualConnectionHandler, 
			String sessionId, DiameterAnswer remoteDiameterAnswer) {
		
		addMandatoryAVPs(remoteDiameterAnswer, currentVirtualConnectionHandler.getHostName(), 
				sessionId);
		currentVirtualConnectionHandler.getInputStream().received(remoteDiameterAnswer);
	}
	
	private void addMandatoryAVPs(DiameterAnswer translatedAnswer, String hostIdentity, String sessionId) {
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ORIGIN_HOST, translatedAnswer, hostIdentity);
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ORIGIN_REALM, translatedAnswer, Parameter.getInstance().getOwnDiameterRealm());
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.SESSION_ID, translatedAnswer, sessionId);
	}

	class RadiusBroadcastResponseListenerInternal implements RadResponseListener {

		private final ApplicationResponse originalResponse;
		private final DiameterBroadcastResponseListener listener;
		private final int order;
		private DiameterAnswer remoteDiameterAnswer;
		private DiaToRadTranslationHelper translationHelper;

		private VirtualConnectionHandler currentConnectionHandler;
		private DiameterRequest remoteDiameterRequest;
		private ApplicationRequest originalRequest;

		public RadiusBroadcastResponseListenerInternal(
				ApplicationRequest originalRequest,
				ApplicationResponse originalResponse,
				DiameterRequest remoteDiameterRequest,
				DiameterBroadcastResponseListener listener, 
				DiaToRadTranslationHelper translationHelper, 
				int order) {

			this.originalRequest = originalRequest;
			this.originalResponse = originalResponse;
			this.remoteDiameterRequest = remoteDiameterRequest;
			this.listener = listener;
			this.translationHelper = translationHelper;
			this.order = order;
			this.remoteDiameterAnswer = DiameterUtility.newAnswerWithHeaderOf(remoteDiameterRequest);
		}

		public void setVirtualConnectionHandler(VirtualConnectionHandler virtualConnectionHandler) {
			this.currentConnectionHandler = virtualConnectionHandler;
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest, RadUDPResponse radUDPResponse, ISession session) {
			listener.addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor.from(
					new BroadcastTranslationExecutor((RadiusPacket)radUDPResponse.getRadiusPacket(), 
							remoteDiameterAnswer, originalResponse, translationHelper),
					order));
			
			sendToVirtualStream(currentConnectionHandler, originalRequest.getDiameterRequest().getSessionID(), remoteDiameterAnswer);
			
		}

		/**
		 * This event will occur only in case Radius ESI is dead 
		 * and Virtual Peer of that respective ESI is open. 
		 * This scenario will be replicated when ESIEventListner has not yet received ESI dead event. 
		 */
		// FIXME don't yet know what to do :(:(:(
		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
					remoteDiameterAnswer, ResultCode.DIAMETER_UNABLE_TO_DELIVER.code+ "");
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
					remoteDiameterAnswer, DiameterErrorMessageConstants.DEST_PEER_NOT_FOUND);

			listener.addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor.from(
					new DiameterAsyncRequestExecutor() {

						@Override
						public void handleServiceRequest(ApplicationRequest serviceRequest, ApplicationResponse serviceResponse,
								ISession session) {
							aggregate(serviceResponse, remoteDiameterAnswer);
						}
					},
					order));
			
//			sendToVirtualStream(currentConnectionHandler, session, remoteDiameterAnswer);
		}

		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {

			if (data.isAcceptableResultCode(ResultCode.DIAMETER_REQUEST_TIMEOUT.code) == false) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Timeout occured for requeoast with Session-Id=" 
							+ remoteDiameterRequest.getSessionID() 
							+ ", sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER 
							+ ", as accept on timeout is disabled.");
				}

				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
						remoteDiameterAnswer, ResultCode.DIAMETER_UNABLE_TO_DELIVER.code+ "");
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
						remoteDiameterAnswer, DiameterErrorMessageConstants.REQUEST_TIMEOUT);

			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Timeout occured for request with Session-Id=" 
							+ remoteDiameterRequest.getSessionID() 
							+ ", sending " + ResultCode.DIAMETER_SUCCESS 
							+ ", as accept on timeout is enabled.");
				}
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
						remoteDiameterAnswer, ResultCode.DIAMETER_SUCCESS.code+ "");
			}
			listener.addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor.from(
					new DiameterAsyncRequestExecutor() {

						@Override
						public void handleServiceRequest(ApplicationRequest serviceRequest,
								ApplicationResponse serviceResponse, ISession session) {
							aggregate(serviceResponse, remoteDiameterAnswer);
						}					
					}, order));
//			sendToVirtualStream(currentConnectionHandler, session, remoteDiameterAnswer);
		}

	}
	class BroadcastTranslationExecutor implements DiameterAsyncRequestExecutor {


		private final RadiusPacket remoteRadiusResponse;
		private final ApplicationResponse originalResponse;
		private final DiaToRadTranslationHelper translationHelper;
		private DiameterAnswer remoteDiameterAnswer;

		public BroadcastTranslationExecutor(RadiusPacket remoteRadiusResponse, DiameterAnswer remoteDiameterAnswer,
				ApplicationResponse originalResponse, DiaToRadTranslationHelper translationHelper) {

			this.remoteRadiusResponse = remoteRadiusResponse;
			this.remoteDiameterAnswer = remoteDiameterAnswer;
			this.originalResponse = originalResponse;
			this.translationHelper = translationHelper;
		}

		@Override
		public void handleServiceRequest(ApplicationRequest serviceRequest, ApplicationResponse serviceResponse,
				ISession session) {

			try {
				// response translation
				remoteDiameterAnswer = translationHelper.translateAnswer(remoteRadiusResponse, remoteDiameterAnswer);
			} catch (TranslationFailedException e) {
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
						remoteDiameterAnswer, ResultCode.DIAMETER_UNABLE_TO_COMPLY.code+ "");
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
						remoteDiameterAnswer, DiameterErrorMessageConstants.translationFailed(data.getTranslationMapping()));
			}

			aggregate(originalResponse, remoteDiameterAnswer);

			if (session != null && serviceContext.getSessionReleaseIndicator().isEligible(remoteDiameterAnswer)) {
				session.release();
			}
		}
	}

	class DiaToRadTranslationHelper {
		private final DiameterRequest remoteRequest;
		private RadiusPacket remoteRadiusRequest;
		private String selectedRequestMapping;
		private boolean dummyMappingApplicable;

		public DiaToRadTranslationHelper(DiameterRequest remoteRequest) {
			this.remoteRequest = remoteRequest;
		}

		public boolean isDummyMappingApplicable() {
			return dummyMappingApplicable;
		}

		public RadiusPacket translateRequest() throws TranslationFailedException {
			
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Translating request using translation policy: " 
						+ data.getTranslationMapping());
			}
			
			remoteRadiusRequest = new RadiusPacket();
			TranslatorParams params = new TranslatorParamsImpl(remoteRequest, remoteRadiusRequest);

			translationAgent.translate(data.getTranslationMapping(), params, true);

			dummyMappingApplicable = Boolean.parseBoolean(String.valueOf(params.getParam(TranslatorConstants.DUMMY_MAPPING)));
			selectedRequestMapping = (String) params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);
			
			byte[] authenticator = remoteRadiusRequest.getAuthenticator();
			
			if (remoteRadiusRequest.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD) != null) {
				String strPassword = new String(remoteRadiusRequest.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD).getValueBytes());
				byte [] bEncrypatedPassword = RadiusUtility.encryptPasswordRFC2865(strPassword.trim(), authenticator, AAAServerConstants.DIAMETER_TO_RADIUS_SECRET);
				remoteRadiusRequest.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD).setValueBytes(bEncrypatedPassword);
			} else if (remoteRadiusRequest.getRadiusAttribute(RadiusAttributeConstants.CHAP_PASSWORD) != null) {
				if (remoteRadiusRequest.getRadiusAttribute(RadiusAttributeConstants.CHAP_CHALLENGE) == null) {
					IRadiusAttribute attr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_CHALLENGE);
					attr.setValueBytes(authenticator);

					remoteRadiusRequest.addAttribute(attr);
				}
			}
			
			remoteRadiusRequest.refreshPacketHeader();
			
			return remoteRadiusRequest;
		}

		public DiameterAnswer translateAnswer(@Nullable RadiusPacket remoteRadiusResponse, 
				DiameterAnswer remoteDiameterAnswer) throws TranslationFailedException {
			
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Translating answer using translation policy: " 
						+ data.getTranslationMapping());
			}
			
			TranslatorParamsImpl params = new TranslatorParamsImpl(remoteRadiusResponse, 
					remoteDiameterAnswer, 
					remoteRequest, 
					remoteRadiusRequest);

			params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, selectedRequestMapping);

			/** set shared secret in translatorParams which will be used in DiaToRadiusTranslator's translateResponse method */
			params.setParam(TranslatorConstants.SHARED_SECRET, remoteRequest.getParameter(TranslatorConstants.SHARED_SECRET));
			translationAgent.translate(data.getTranslationMapping(), params, TranslatorConstants.RESPONSE_TRANSLATION);

			return remoteDiameterAnswer;
		}
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return group.isAlive() == false;
	}
}
