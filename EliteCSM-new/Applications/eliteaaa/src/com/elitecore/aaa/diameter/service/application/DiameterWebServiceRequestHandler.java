package com.elitecore.aaa.diameter.service.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.server.axixserver.IDiameterWSRequestHandler;
import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.VirtualInputStream;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class DiameterWebServiceRequestHandler implements IDiameterWSRequestHandler {

	public static final String MODULE = "DIA-WS-LSNR";
	private static final int REQUEST_TIMEOUT_MS = 3000;

	private VirtualInputStream virtualInputStream;
	private final Map<Integer, SettableFuture<DiameterAnswer>> hbhToFutureResponse;
	
	private final EliteDiameterStack diameterStack;
	private final DiameterStackConfigurable diameterStackConfiguration;
	
	public DiameterWebServiceRequestHandler(final EliteDiameterStack diameterStack,
			final DiameterStackConfigurable diameterStackConfiguration) throws ElementRegistrationFailedException {
		this.diameterStack = diameterStack;
		this.diameterStackConfiguration = diameterStackConfiguration;
		registerVirtualPeer();
		this.hbhToFutureResponse = new ConcurrentHashMap<Integer, SettableFuture<DiameterAnswer>>();
	}


	private void registerVirtualPeer() throws ElementRegistrationFailedException {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Initializing diameter web service Virtual Peer."); 
		}
		PeerDataImpl peerData = new PeerDataImpl();
		peerData.setPeerName("diameter.webservice.peer");
		peerData.setHostIdentity("diameter.webservice.peer");
		peerData.setSecurityStandard(SecurityStandard.NONE);
		peerData.setRemoteIPAddress("localhost");
		peerData.setInitiateConnectionDuration(0);
		peerData.setWatchdogInterval(0);
		
		this.virtualInputStream = ((AAAServerContext) diameterStack.getServerContext())
				.registerVirtualPeer(peerData, new VirtualOutputStream() {

			@Override
			public void send(Packet diaPacket) {
				DiameterAnswer answer = (DiameterAnswer) diaPacket;

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Diameter Answer received with HbH-ID=" 
							+ answer.getHop_by_hopIdentifier() + ", Session-ID="
							+ answer.getAVPValue(DiameterAVPConstants.SESSION_ID));
				}
				
				SettableFuture<DiameterAnswer> future = hbhToFutureResponse.remove(answer.getHop_by_hopIdentifier());
				/*
				 * Future may be null in cases when timeout occurs waiting for diameter answer,
				 * and stack replies post timeout. In that case we just need to ignore.
				 */
				if (future == null) {
					return;
				}
				future.set(answer);
			}
		});
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Diameter web service Virtual Peer initialized successfully"); 
		}
	}

	@Override
	public @Nullable String getMappingConfigId(String methodId) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Getting Web-Service translation policy for: " 
								+ methodId);
		}
		String wsTranslationMappingName = diameterStackConfiguration.getWsTranslationMappingName(methodId);
		if (Strings.isNullOrBlank(wsTranslationMappingName)) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Web-Service translation policy not configured in Diameter Stack for: " 
								+ methodId);
			}
		}
		return wsTranslationMappingName;
	}

	@Override
	public DiameterAnswer submitToStack(DiameterRequest diameterRequest) {
		DiameterAnswer response = waitForResponse(diameterRequest, 
				submitNonBlockingToStack(diameterRequest));
		hbhToFutureResponse.remove(diameterRequest.getHop_by_hopIdentifier());
		return response;
	}
	
	@Override
	public SettableFuture<DiameterAnswer> submitNonBlockingToStack(DiameterRequest diameterRequest) {
	
		final SettableFuture<DiameterAnswer> futureResponse = SettableFuture.create();
		hbhToFutureResponse.put(diameterRequest.getHop_by_hopIdentifier(), futureResponse);
		virtualInputStream.received(diameterRequest);
		return futureResponse;
	}


	private DiameterAnswer waitForResponse(DiameterRequest diameterRequest,
			final SettableFuture<DiameterAnswer> future) {
		
		DiameterAnswer ans;
		
		try {
			ans = future.get(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			ans = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY); 
			LogManager.getLogger().debug(MODULE, "Interrupted waiting for diameter answer.");
		} catch (TimeoutException e) {
			ans = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
			if(LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Timeout occured handling Diameter Request with HbH-ID=" 
						+ diameterRequest.getHop_by_hopIdentifier() + ", Session-ID="
						+ diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
		} catch (ExecutionException e) {
			ans = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
			LogManager.getLogger().trace(MODULE, e);
		}
		return ans;
	}
}
