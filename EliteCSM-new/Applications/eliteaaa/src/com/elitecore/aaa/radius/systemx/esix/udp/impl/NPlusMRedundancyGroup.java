package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.List;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CommunicatorData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.loadBalancer.LoadBalancer;
import com.elitecore.core.systemx.esix.loadBalancer.RoundRobinLoadBalancer;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
/**
 * <p> This group is working as N - M redundancy mode.
 * </p>
 * 
 * @author soniya.patel
 * 
 */

public class NPlusMRedundancyGroup extends RadiusEsiGroup {

	private static final String MODULE = "N-M REDUNDANCY GROUP";

	private LoadBalancer<UDPCommunicator> primaryLoadBalancer;
	private LoadBalancer<UDPCommunicator> failOverLoadBalancer;

	public NPlusMRedundancyGroup(AAAServerContext serverContext, RadiusEsiGroupData esiGroupData) {
		super(serverContext, esiGroupData);

		this.primaryLoadBalancer = new RoundRobinLoadBalancer<>(true);
		this.failOverLoadBalancer = new RoundRobinLoadBalancer<>(true);
	}

	@Override
	public boolean isAlive() {
		return primaryLoadBalancer.isAlive() || failOverLoadBalancer.isAlive();
	}

	@Override
	public void init() throws InitializationFailedException {
		addPrimaryCommunicators();
		addFailOverCommunicators();
	}

	@Override
	protected void loadBalanceInitialRequest(byte[] requestBytes, String secret, RadResponseListener responseListner,
			ISession session) {

		boolean requestSent =	loadBalanceRequestUsingPrimaryEsiGroup(requestBytes, secret, responseListner, session);

		if (!requestSent) {
			loadBalanceRequestUsingFailOverEsiGroup(requestBytes, secret, responseListner, session);
		}

	}

	@Override
	protected void sendSubsequentRequest(byte[] requestBytes, String secret, RadResponseListener responseListner,
			ISession session) {

		String esiName = (String)session.getParameter(getActiveEsiKey());

		boolean requestSent = subsequentRequestSentToEsiOfPrimaryEsiGroup(requestBytes, secret, responseListner, session, esiName);

		if (!requestSent) {
			requestSent = subsequentRequestSentToEsiOfFailOverEsiGroup(requestBytes, secret, responseListner, session, esiName);
		}

		if(!requestSent) {
			responseListner.requestDropped(new RadUDPRequestImpl(requestBytes, secret));
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "No stateful alive communicator is found, so request dropped");
			}
		}

	}

	private boolean subsequentRequestSentToEsiOfPrimaryEsiGroup(byte[] requestBytes, String secret,
			RadResponseListener responseListner, ISession session, String esiName) {
		boolean requestSent = false;

		UDPCommunicator udpCommunicator = getCommunicatorLocator().getStatefulEsiFromPrimaryGroup(esiName, requestBytes);
		String standByEsiName = (String) session.getParameter(getStandByEsiKey());

		if (udpCommunicator != null) {
			if (udpCommunicator.isAlive()) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Sending request to stateful primary esi: " + udpCommunicator.getName());
				}
				udpCommunicator.handleUDPRequest(new RadUDPRequestImpl(requestBytes, secret, new FailOverListener(responseListner, session.getSessionId())));

				session.removeParameter(getStandByEsiKey());
				requestSent = true;

			}  else if (getRadiusEsiGroupData().isSwitchBackEnable() && !Strings.isNullOrBlank(standByEsiName)) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Sending request to stateful fail-over esi as primary esi is not alive: " + udpCommunicator.getName());
				}

				requestSent = subsequentRequestSentToEsiOfFailOverEsiGroup(requestBytes, secret, responseListner, session, standByEsiName);

			} else {
				loadBalanceRequestUsingFailOverEsiGroup(requestBytes, secret, responseListner, session);
				requestSent = true;
			}
		}

		return requestSent;
	}

	private boolean subsequentRequestSentToEsiOfFailOverEsiGroup(byte[] requestBytes, String secret,
			RadResponseListener responseListner, ISession session, String esiName) {

		UDPCommunicator udpCommunicator = getCommunicatorLocator().getStatefulEsiFromFailOverGroup(esiName, requestBytes);

		boolean requestSent = false;

		if (udpCommunicator != null && udpCommunicator.isAlive()) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending request to stateful fail-over esi: " + udpCommunicator.getName());
			}
			udpCommunicator.handleUDPRequest(new RadUDPRequestImpl(requestBytes, secret, new UDPResponseListenerImpl(responseListner, session.getSessionId())));
			requestSent = true;
		} 

		return requestSent;
	}

	private boolean loadBalanceRequestUsingPrimaryEsiGroup(byte[] requestBytes, String secret, RadResponseListener responseListner, ISession session) {
		boolean requestSent = false;

		UDPCommunicator communicator = getCommunicator(primaryLoadBalancer);

		if (communicator != null) {

			session.setParameter(getActiveEsiKey(), communicator.getName());

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending request to primary esi: " + communicator.getName());
			}

			communicator.handleUDPRequest(new RadUDPRequestImpl(requestBytes, secret, new FailOverListener(responseListner, session.getSessionId())));
			requestSent = true;
		}

		return requestSent;
	}

	private void loadBalanceRequestUsingFailOverEsiGroup(byte[] requestBytes, String secret,
			RadResponseListener responseListner, ISession session) {
		UDPCommunicator communicator = getCommunicator(failOverLoadBalancer);

		if (communicator == null) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "No alive communicator found, so dropping request");
			}
			responseListner.requestDropped(new RadUDPRequestImpl(requestBytes, secret));
		} else {
			String activeEsiValue = (String) session.getParameter(getActiveEsiKey());

			if (getRadiusEsiGroupData().isSwitchBackEnable() && !Strings.isNullOrBlank(activeEsiValue)) {
				session.setParameter(getStandByEsiKey(), communicator.getName());
			} else {
				session.setParameter(getActiveEsiKey(), communicator.getName());
			}

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending request to fail-over esi: " + communicator.getName());
			}

			communicator.handleUDPRequest(new RadUDPRequestImpl(requestBytes, secret, new UDPResponseListenerImpl(responseListner, session.getSessionId())));
		}
	}

	private void addPrimaryCommunicators() throws InitializationFailedException {
		List<CommunicatorData> primaryEsiList = getRadiusEsiGroupData().getPrimaryEsiList();

		for(CommunicatorData commData: primaryEsiList) {
			UDPCommunicator communicator = getOrCreateCommunicator(commData.getName());
			primaryLoadBalancer.addCommunicator(communicator, commData.getLoadFactor());
			nameToCommunicatorMapForPrimaryEsiGroup.put(communicator.getName(), communicator);
		}
	}

	private void addFailOverCommunicators() throws InitializationFailedException {
		List<CommunicatorData> failOverList = getRadiusEsiGroupData().getFailOverEsiList();

		for(CommunicatorData commData: failOverList) {
			UDPCommunicator communicator = getOrCreateCommunicator(commData.getName());
			failOverLoadBalancer.addCommunicator(communicator, commData.getLoadFactor());
			nameToCommunicatorMapForFailOverEsiGroup.put(communicator.getName(), communicator);
		}
	}

	private class FailOverListener extends UDPResponseListenerImpl {

		public FailOverListener(RadResponseListener radResponseListener, String sessionId) {
			super(radResponseListener, sessionId);
		}

		@Override	
		public void requestTimeout(UDPRequest udpRequest, UDPCommunicator primaryCommunicator) {

			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, requestTimeOutMessage(udpRequest, primaryCommunicator));
			}

			generateTimeoutAlert(primaryCommunicator);

			removeProxyStateAvp(primaryCommunicator.getName(), udpRequest);

			RadUDPRequest radUdpRequest = (RadUDPRequest) udpRequest;

			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Load balancing request.");
			}

			UDPCommunicator secondaryCommunicator = getCommunicator(failOverLoadBalancer);

			if (secondaryCommunicator != null) {
				if (getRadiusEsiGroupData().isStateful()) {
					updateSession(secondaryCommunicator.getName(), radUdpRequest, sessionId);
				}

				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Sending request to fail-over esi: " + secondaryCommunicator.getName());
				}

				secondaryCommunicator.handleUDPRequest(new RadUDPRequestImpl(udpRequest.getBytes(), ((RadUDPRequest)udpRequest).getSharedSecret(), new UDPResponseListenerImpl(radResponseListener, sessionId)));
			} else {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "No alive communicator found from fail-over esi, so dropping request");
				}
				radResponseListener.requestTimeout(radUdpRequest);
			}
		}

	}
}