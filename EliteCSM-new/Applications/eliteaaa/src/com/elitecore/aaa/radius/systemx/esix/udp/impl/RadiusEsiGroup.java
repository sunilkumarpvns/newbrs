package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.esi.radius.conf.impl.CorrelatedRadiusData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommGroup;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.loadBalancer.LoadBalancer;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.UDPResponseListener;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusPacketTypeConstant;

public abstract class RadiusEsiGroup implements RadUDPCommGroup {

	private static final String MODULE = "RAD-ESI-COMM-GRP";

	protected  Map<String, UDPCommunicator> nameToCommunicatorMapForPrimaryEsiGroup;
	protected  Map<String, UDPCommunicator> nameToCommunicatorMapForFailOverEsiGroup;
	protected Map<String, String> correlatedRadiusCommunicatorName;
	private AAAServerContext serverContext;
	private RadiusEsiGroupData radiusEsiGroupData;
	private final String activeEsiKey;
	private final String standByEsiKey;
	private CommunicatorLocator communicatorLocator;
	protected StateBehaviour stateBehaviour;

	public RadiusEsiGroup(AAAServerContext serverContext, RadiusEsiGroupData radiusEsiGroupData) {
		this.serverContext = serverContext;
		this.radiusEsiGroupData = radiusEsiGroupData;
	
		this.nameToCommunicatorMapForPrimaryEsiGroup = new HashMap<>();
		this.nameToCommunicatorMapForFailOverEsiGroup = new HashMap<>();
		this.correlatedRadiusCommunicatorName = new HashMap<>();
		
		this.activeEsiKey = radiusEsiGroupData.getName() + "-active esi";
		this.standByEsiKey = radiusEsiGroupData.getName() + "-stand by esi";
		
		if (getRadiusEsiGroupData().isStateful()) {
			this.stateBehaviour = (requestBytes, secret, responseListner, session) -> {
				
				if (isIntialRequest(session)) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Load balancing request.");
					}
					loadBalanceInitialRequest(requestBytes, secret, responseListner, session);
				} else {
					sendSubsequentRequest(requestBytes, secret, responseListner, session);
				}
			};

		} else {
			this.stateBehaviour = this::loadBalanceInitialRequest;
		}
		
		if (EsiType.CORRELATED_RADIUS.typeName.equalsIgnoreCase(getRadiusEsiGroupData().getEsiType())) {
			this.communicatorLocator = new CommunicatorLocatorForRadius();
		} else {
			this.communicatorLocator = new CommunicatorLocatorForEsi();
		}
	}
	
	@Override
	public void handleRequest(byte[] requestBytes, String secret, RadResponseListener responseListner,
			ISession session) {
		
		this.stateBehaviour.handleRequest(requestBytes, secret, responseListner, session);
	}
	
	
	protected final String getActiveEsiKey() {
		return activeEsiKey;
	}
	
	protected final String getStandByEsiKey() {
		return standByEsiKey;
	}

	protected final UDPCommunicator getOrCreateCommunicator(String esiName) throws InitializationFailedException {

		if (EsiType.CORRELATED_RADIUS.typeName.equalsIgnoreCase(getRadiusEsiGroupData().getEsiType())) {
			return getCorrelatedRadiusCommunicator(esiName);
		} else {
			return getCommunicator(esiName);
		}

	}

	private UDPCommunicator getCorrelatedRadiusCommunicator(String esiName) throws InitializationFailedException {

		CorrelatedRadiusData correlatedRadiusData = getServerContext().getServerConfiguration().getCorrelatedRadiusConfigurable().getCorrelatedRadiusUsingName(esiName);

		if (correlatedRadiusData == null) {
			throw new InitializationFailedException("Configured Correlated Radius not found: " + esiName);
		} else {
			UDPCommunicator authCommunicator = getCommunicator(correlatedRadiusData.getAuthEsiName());
			UDPCommunicator acctCommunicator = getCommunicator(correlatedRadiusData.getAcctEsiName());

			CorrelatedRadiusCommunicator correlatedRadiusCommunicator = new CorrelatedRadiusCommunicator(authCommunicator, acctCommunicator);
			
			correlatedRadiusCommunicatorName.put(authCommunicator.getName(), correlatedRadiusCommunicator.getName());
			correlatedRadiusCommunicatorName.put(acctCommunicator.getName(), correlatedRadiusCommunicator.getName());
			
			return correlatedRadiusCommunicator;
		}
	}

	private UDPCommunicator getCommunicator(String esiName) throws InitializationFailedException {
		Optional<DefaultExternalSystemData> udpES = serverContext.getServerConfiguration().getRadESConfiguration().getESDataByName(esiName);

		if (udpES.isPresent()) {
			return serverContext.getRadUDPCommunicatorManager().findCommunicatorByIDOrCreate(udpES.get().getUUID(),
					serverContext, udpES.get());
		} else {
			throw new InitializationFailedException("Configured External system interface is not found: " + esiName);
		}
	}

	protected final AAAServerContext getServerContext() {
		return serverContext;
	}

	public RadiusEsiGroupData getRadiusEsiGroupData() {
		return radiusEsiGroupData;
	}

	protected UDPCommunicator getCommunicator(LoadBalancer<UDPCommunicator> loadBalancer) {
		return loadBalancer.getCommunicator();
	}

	protected abstract void init() throws InitializationFailedException;
	
	protected abstract void loadBalanceInitialRequest(byte[] requestBytes, String secret, RadResponseListener responseListner, ISession session);
	
	protected abstract void sendSubsequentRequest(byte[] requestBytes, String secret, RadResponseListener responseListner, ISession session);


	@Override
	public void addCommunicator(UDPCommunicator esCommunicator, int weightage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addCommunicator(UDPCommunicator esCommunicator) {
		throw new UnsupportedOperationException();
	}

	public class UDPResponseListenerImpl implements UDPResponseListener {
		protected RadResponseListener radResponseListener;
		protected String sessionId;

		public UDPResponseListenerImpl(RadResponseListener radResponseListener, String sessionId) {
			this.radResponseListener = radResponseListener;
			this.sessionId = sessionId;
		}

		@Override
		public void responseReceived(UDPRequest udpRequest, UDPResponse udpResponse) {
			RadUDPResponse radUDPResponse = (RadUDPResponse) udpResponse;
			RadUDPRequest radUDPRequest = (RadUDPRequest) udpRequest;
			IRadiusAttribute satisfiedESI = Dictionary.getInstance().getKnownAttribute(
					RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_SATISFIED_ESI);
			if (satisfiedESI != null) {
				satisfiedESI.setStringValue(udpResponse.getCommunicatorName());
				radUDPResponse.getRadiusPacket().addInfoAttribute(satisfiedESI);
			}
			radResponseListener.responseReceived(radUDPRequest, radUDPResponse, HazelcastRadiusSession.RAD_NO_SESSION);
		}

		@Override
		public void requestTimeout(UDPRequest udpRequest, UDPCommunicator udpCommunicator) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, requestTimeOutMessage(udpRequest, udpCommunicator));
			}

			generateTimeoutAlert(udpCommunicator);

			removeProxyStateAvp(udpCommunicator.getName(), udpRequest);

			radResponseListener.requestTimeout((RadUDPRequest)udpRequest);	
		}

		@Override
		public void requestDropped(UDPRequest udpRequest, UDPCommunicator udpCommunicator) {
			radResponseListener.requestDropped((RadUDPRequest)udpRequest);
		}

	}

	protected void updateSession(String secondaryCommunicatorName, RadUDPRequest radUdpRequest, String sessionId) {
		ISession session = getServerContext().getOrCreateRadiusSession(sessionId);

		if (getRadiusEsiGroupData().isSwitchBackEnable()) {
			session.setParameter(standByEsiKey, secondaryCommunicatorName);
		} else {
			session.setParameter(activeEsiKey, secondaryCommunicatorName);
		}

		session.update(new RadiusSessionDataTranslatedValueProvider(radUdpRequest));
	}

	protected String requestTimeOutMessage(UDPRequest udpRequest, UDPCommunicator udpCommunicator) {
		return "Timeout response received from server: " + udpCommunicator.getName() + " ["
				+ udpCommunicator.getCommunicatorContext().getIPAddress() + ":"
				+ udpCommunicator.getCommunicatorContext().getPort() + "] " + "for request id: "
				+ udpRequest.getIdentifier();
	}

	protected void generateTimeoutAlert(UDPCommunicator udpCommunicator) {
		getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.RADIUS_REQUEST_TIMEOUT, MODULE,
				"Timeout response received from server: "
						+ udpCommunicator.getCommunicatorContext().getIPAddress() + ":"
						+ udpCommunicator.getCommunicatorContext().getPort(),
						0, udpCommunicator.getCommunicatorContext().getIPAddress() + ":"
								+ udpCommunicator.getCommunicatorContext().getPort());
	}

	protected void removeProxyStateAvp(String communicatorName, UDPRequest udpRequest) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Removing proxy state attribute from request which "
					+ "has been added by : " + communicatorName);
		}

		IRadiusPacket radiusPacket = ((RadUDPRequest)udpRequest).getRadiusPacket();

		List<IRadiusAttribute> receivedProxyStates = radiusPacket.getRadiusAttributes(RadiusAttributeConstants.PROXY_STATE);

		if (Collectionz.isNullOrEmpty(receivedProxyStates)) {
			return;
		}

		IRadiusAttribute primaryESIsProxyState = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.PROXY_STATE);

		if (primaryESIsProxyState == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,
						"Proxy state attribute will not be removed from request as it is not found in dictionary");
			}
			return;
		}

		primaryESIsProxyState.setStringValue(String.valueOf(udpRequest.getRequestSentTime()));

		IRadiusAttribute ownProxyState = receivedProxyStates.get(receivedProxyStates.size() - 1);

		if (ownProxyState.equals(primaryESIsProxyState)) {
			radiusPacket.removeAttribute(ownProxyState);

			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,
						"Removed own proxy state attribute with value: " + ownProxyState.getStringValue()
						+ " from request for ID: " + udpRequest.getIdentifier());
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Own proxy state attribute with value: "
						+ ownProxyState.getStringValue()
						+ " does not match with proxy state attribute which is added by primary communicator");
			}
		}
	}

	protected boolean isIntialRequest(ISession session) {
		return Strings.isNullOrBlank((String) session.getParameter(activeEsiKey));
	}

	public CommunicatorLocator getCommunicatorLocator() {
		return communicatorLocator;
	}

	public enum EsiType {

		AUTH("AUTH"),
		ACCT("ACCT"),
		CHARGING_GATEWAY("CHARGING GATEWAY"),
		CORRELATED_RADIUS("CORRELATED RADIUS");

		public final String typeName;

		private EsiType(String typeName) {
			this.typeName = typeName;
		}

	}

	public enum RedundancyMode {

		NM("N+M"),
		ACTIVE_PASSIVE("ACTIVE-PASSIVE");

		public final String redundancyModeName;

		private RedundancyMode (String redundancyMode) {
			this.redundancyModeName = redundancyMode;
		}
	}

	public class CommunicatorLocatorForRadius implements CommunicatorLocator {

		@Override
		public UDPCommunicator getStatefulEsiFromPrimaryGroup(String esiName, byte[] requestBytes) {
			UDPCommunicator udpCommunicator = nameToCommunicatorMapForPrimaryEsiGroup.get(esiName);

			if (udpCommunicator != null) {
				RadiusPacket radiusPacket = new RadiusPacket();
				radiusPacket.setBytes(requestBytes);

				if (RadiusPacketTypeConstant.ACCESS_REQUEST.packetTypeId == radiusPacket.getPacketType()) {
					udpCommunicator =  ((CorrelatedRadiusCommunicator) udpCommunicator).getAuthCommunicator();
				} else {
					udpCommunicator =  ((CorrelatedRadiusCommunicator) udpCommunicator).getAcctCommunicator();
				}
			}
			return udpCommunicator;
		}

		@Override
		public UDPCommunicator getStatefulEsiFromFailOverGroup(String esiName, byte[] requestBytes) {
			UDPCommunicator udpCommunicator = nameToCommunicatorMapForFailOverEsiGroup.get(esiName);

			if (udpCommunicator != null) {
				RadiusPacket radiusPacket = new RadiusPacket();
				radiusPacket.setBytes(requestBytes);

				if (RadiusPacketTypeConstant.ACCESS_REQUEST.packetTypeId == radiusPacket.getPacketType()) {
					udpCommunicator = ((CorrelatedRadiusCommunicator) udpCommunicator).getAuthCommunicator();
				} else {
					udpCommunicator = ((CorrelatedRadiusCommunicator) udpCommunicator).getAcctCommunicator();
				}
			}

			return udpCommunicator;
		}

		@Override
		public String getCommunicatorName(UDPCommunicator udpCommunicator) {
			return correlatedRadiusCommunicatorName.get(udpCommunicator.getName());
		}

	}

	public class CommunicatorLocatorForEsi implements CommunicatorLocator {

		@Override
		public UDPCommunicator getStatefulEsiFromPrimaryGroup(String esiName, byte[] requestBytes) {
			return nameToCommunicatorMapForPrimaryEsiGroup.get(esiName);
		}

		@Override
		public UDPCommunicator getStatefulEsiFromFailOverGroup(String esiName, byte[] requestBytes) {
			return nameToCommunicatorMapForFailOverEsiGroup.get(esiName);
		}

		@Override
		public String getCommunicatorName(UDPCommunicator udpCommunicator) {
			return udpCommunicator.getName();
		}

	}

	interface CommunicatorLocator {

		UDPCommunicator getStatefulEsiFromPrimaryGroup(String esiName, byte[] requestBytes);
		UDPCommunicator getStatefulEsiFromFailOverGroup(String esiName, byte[] requestBytes);
		String getCommunicatorName(UDPCommunicator udpCommunicator);

	}
	
	interface StateBehaviour {
		void handleRequest(byte[] requestBytes, String secret, RadResponseListener responseListner, ISession session);
	}

}
