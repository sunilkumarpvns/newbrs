package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ActivePassiveCommunicatorData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.loadBalancer.LoadBalancer;
import com.elitecore.core.systemx.esix.loadBalancer.RoundRobinLoadBalancer;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorContext;
import com.elitecore.core.systemx.esix.udp.UDPRequest;

/**
 * <p> This group is working as Active-Passive redundancy mode.</p>
 * 
 * @author soniya.patel
 * 
 */

public class ActivePassiveRedundancyGroup extends RadiusEsiGroup {

	private static final String MODULE = "ACTIVE PASSIVE REDUNDANCY GROUP";

	private LoadBalancer<UDPCommunicator> primaryLoadBalancer;
	private Map<String, String> activeEsiNameToPassiveEsiNameMapping;

	public ActivePassiveRedundancyGroup(AAAServerContext serverContext, RadiusEsiGroupData esiGroupData) {
		super(serverContext, esiGroupData);

		this.activeEsiNameToPassiveEsiNameMapping = new HashMap<>();
		this.primaryLoadBalancer = new RoundRobinLoadBalancer<>(true);
	}

	@Override
	public boolean isAlive() {
		return primaryLoadBalancer.isAlive();
	}

	@Override
	public void init() throws InitializationFailedException {
		List<ActivePassiveCommunicatorData> activePassiveEsiList = getRadiusEsiGroupData().getActivePassiveEsiList();
		
		for (ActivePassiveCommunicatorData activePassiveEsiData: activePassiveEsiList) {

			UDPCommunicator activeCommunicator = getOrCreateCommunicator(activePassiveEsiData.getActiveEsiName());
			UDPCommunicator passiveCommunicator = getOrCreateCommunicator(activePassiveEsiData.getPassiveEsiName());

			UDPCommunicator activePassiveGroup = new ActivePassiveCommunicator(activeCommunicator, passiveCommunicator);
			primaryLoadBalancer.addCommunicator(activePassiveGroup, activePassiveEsiData.getLoadFactor());

			nameToCommunicatorMapForPrimaryEsiGroup.put(activeCommunicator.getName(), activeCommunicator);
			nameToCommunicatorMapForFailOverEsiGroup.put(passiveCommunicator.getName(), passiveCommunicator);
			activeEsiNameToPassiveEsiNameMapping.put(activeCommunicator.getName(), passiveCommunicator.getName());
		}
	}

	@Override
	protected void loadBalanceInitialRequest(byte[] requestBytes, String secret, RadResponseListener responseListner,
			ISession session) {
		ActivePassiveCommunicator communicator = (ActivePassiveCommunicator) getCommunicator(primaryLoadBalancer);

		if (communicator == null) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "No alive communicator found, so dropping request");
			}
			responseListner.requestDropped(new RadUDPRequestImpl(requestBytes, secret));
		} else {
			communicator.handleUDPRequest(requestBytes, secret, responseListner, session);
		}
	}

	@Override
	protected void sendSubsequentRequest(byte[] requestBytes, String secret, RadResponseListener responseListner,
			ISession session) {

		String esiName = (String) session.getParameter(getActiveEsiKey());

		boolean requestSent = sendSubSequentRequestToActiveEsi(requestBytes, secret, responseListner, session, esiName);

		if (!requestSent) {
			requestSent = sendSubSequentRequestToPassiveEsi(requestBytes, secret, responseListner, session, esiName);
		}

		if (!requestSent) {
			responseListner.requestDropped(new RadUDPRequestImpl(requestBytes, secret));
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "No stateful alive communicator is found, so request dropped");
			}
		}
	}

	private boolean sendSubSequentRequestToActiveEsi(byte[] requestBytes, String secret,
			RadResponseListener responseListner, ISession session, String esiName) {
		boolean requestSent = false;

		UDPCommunicator udpCommunicator = getCommunicatorLocator().getStatefulEsiFromPrimaryGroup(esiName, requestBytes);

		if (udpCommunicator != null) {
			if (udpCommunicator.isAlive()) {

				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Sending request to stateful active esi: " + udpCommunicator.getName());
				}

				udpCommunicator.handleUDPRequest(new RadUDPRequestImpl(requestBytes, secret, new FailOverListener(responseListner, activeEsiNameToPassiveEsiNameMapping.get(esiName), session.getSessionId()) ));
				requestSent = true;
			} else {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Sending request to stateful passive esi as active esi is not alive: " + udpCommunicator.getName());
				}
				requestSent = sendSubSequentRequestToPassiveEsi(requestBytes, secret, responseListner, session, activeEsiNameToPassiveEsiNameMapping.get(esiName));
			}
		}
		return requestSent;
	}

	private boolean sendSubSequentRequestToPassiveEsi(byte[] requestBytes, String secret, RadResponseListener responseListner, ISession session, String esiName) {
		UDPCommunicator udpCommunicator = getCommunicatorLocator().getStatefulEsiFromFailOverGroup(esiName, requestBytes);

		boolean reqeustSent = false;

		if (udpCommunicator != null && udpCommunicator.isAlive()) {

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending request to stateful passive esi: " + udpCommunicator.getName());
			}

			if (!getRadiusEsiGroupData().isSwitchBackEnable()) {
				session.setParameter(getActiveEsiKey(), getCommunicatorLocator().getCommunicatorName(udpCommunicator));
			} 

			udpCommunicator.handleUDPRequest(new RadUDPRequestImpl(requestBytes, secret, new UDPResponseListenerImpl(responseListner, session.getSessionId())));
			reqeustSent = true;
		} 
		return reqeustSent;
	}

	private class ActivePassiveCommunicator implements UDPCommunicator {
		private UDPCommunicator activeCommunicator;
		private UDPCommunicator passiveCommunicator;
		private volatile boolean isAlive = true;
		private final List<ESIEventListener<ESCommunicator>> eventListeners = 
				new CopyOnWriteArrayList<>();

		public ActivePassiveCommunicator(UDPCommunicator activeCommunicator, UDPCommunicator passiveCommunicator) {
			this.activeCommunicator = activeCommunicator;
			this.passiveCommunicator = passiveCommunicator;
			ESIEventListerImpl eventListener = new ESIEventListerImpl();
			this.activeCommunicator.addESIEventListener(eventListener);
			this.passiveCommunicator.addESIEventListener(eventListener);
		}

		public void handleUDPRequest(byte[] requestBytes, String secret, RadResponseListener responseListner,
				ISession session) {

			RadUDPRequest remoteRequest = null;

			if (activeCommunicator.isAlive()) {
				session.setParameter(getActiveEsiKey(), activeCommunicator.getName());

				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Sending request to active esi: " + activeCommunicator.getName());
				}

				remoteRequest = new RadUDPRequestImpl(requestBytes, secret, new FailOverListener(responseListner, passiveCommunicator.getName(), session.getSessionId()));
				activeCommunicator.handleUDPRequest(remoteRequest);

			} else if (passiveCommunicator.isAlive()) {

				if (getRadiusEsiGroupData().isSwitchBackEnable() == false) { //NOSONAR
					session.setParameter(getActiveEsiKey(), passiveCommunicator.getName());
				} else {
					session.setParameter(getActiveEsiKey(), activeCommunicator.getName());
				}

				remoteRequest = new RadUDPRequestImpl(requestBytes, secret, new UDPResponseListenerImpl(responseListner, session.getSessionId()));

				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Sending request to passive esi: " + passiveCommunicator.getName());
				}

				passiveCommunicator.handleUDPRequest(remoteRequest);

			} else {
				remoteRequest = new RadUDPRequestImpl(requestBytes, secret);
				responseListner.requestDropped(remoteRequest);
			}

		}

		@Override
		public void init() throws InitializationFailedException {
			// no-op
		}

		@Override
		public boolean isAlive() {
			return isAlive;
		}

		@Override
		public void scan() {
			// no-op
		}

		@Override
		public void addESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
			eventListeners.add(eventListener);
		}

		@Override
		public void removeESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
			eventListeners.remove(eventListener);

		}

		@Override
		public void stop() {
			// no-op
		}

		@Override
		public String getName() {
			return activeCommunicator.getName() + passiveCommunicator.getName();
		}

		@Override
		public String getTypeName() {
			return null;
		}

		@Override
		public ESIStatistics getStatistics() {
			return null;
		}

		@Override
		public void registerAlertListener(AlertListener alertListener) {
			//no-op

		}

		@Override
		public void reInit() throws InitializationFailedException {
			//no-op
		}

		@Override
		public int getMinLocalPort() {
			return 0;
		}

		@Override
		public int getTimeOutRequestCounter() {
			return 0;
		}

		@Override
		public void handleUDPRequest(UDPRequest request) {
			throw new UnsupportedOperationException("This method is unsupported");
		}

		@Override
		public UDPCommunicatorContext getCommunicatorContext() {
			return null;
		}

		@Override
		public void shutdown() {
			//no-op
		}

		class ESIEventListerImpl implements ESIEventListener<ESCommunicator> {

			@Override
			public void alive(ESCommunicator esCommunicator) {

				if (isAlive) {
					return;
				}

				isAlive = true;

				for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
					eventListner.alive(ActivePassiveCommunicator.this);
				}

				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Active-Passive communicator group gets alive");
				}

			}

			@Override
			public void dead(ESCommunicator esCommunicator) {

				if (isAlive) {
					if (activeCommunicator.isAlive() == false && passiveCommunicator.isAlive() == false) {  // NOSONAR

						isAlive = false;

						for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
							eventListner.dead(ActivePassiveCommunicator.this);
						} 
						if (LogManager.getLogger().isInfoLogLevel()) {
							LogManager.getLogger().info(MODULE, "Active-Passive communicator group is dead");
						}
					}
				} 

			}
		}
	}

	public class FailOverListener extends UDPResponseListenerImpl {
		private String secodaryEsiName;

		public FailOverListener(RadResponseListener radResponseListener, String secondaryEsiName, String sessionId) {
			super(radResponseListener, sessionId);
			this.secodaryEsiName = secondaryEsiName;
		}

		@Override
		public void requestTimeout(UDPRequest udpRequest, UDPCommunicator udpCommunicator) {

			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, requestTimeOutMessage(udpRequest, udpCommunicator));
			}

			generateTimeoutAlert(udpCommunicator);

			removeProxyStateAvp(udpCommunicator.getName(), udpRequest);

			RadUDPRequest radUdpRequest = (RadUDPRequest) udpRequest;

			UDPCommunicator secondaryCommunicator = nameToCommunicatorMapForFailOverEsiGroup.get(secodaryEsiName);

			if (secondaryCommunicator != null && secondaryCommunicator.isAlive()) {
				if (getRadiusEsiGroupData().isStateful()) {
					updateSession(secondaryCommunicator.getName(), radUdpRequest, sessionId);
				}
				secondaryCommunicator.handleUDPRequest(new RadUDPRequestImpl(radUdpRequest.getBytes(), radUdpRequest.getSharedSecret(), new UDPResponseListenerImpl(radResponseListener, sessionId)));

			} else {

				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Unable to send request to passive esi: " + secodaryEsiName + ", as it is dead marked.");
				}

				radResponseListener.requestTimeout(radUdpRequest);
			}

		}
	}
}
