package com.elitecore.diameterapi.core.common.peer.group;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESCommunicatorGroup;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.loadBalancer.LoadBalancer;
import com.elitecore.core.systemx.esix.loadBalancer.RoundRobinLoadBalancer;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;

/**
 * 
 * A round robin load balancing group that supports high availability and maintains session statefulness.
 * In case of stateful load balancing group stores {@link HighAvailabilityStatus} in the session mapped
 * to the name of the group as key.
 * 
 * @author narendra.pathai
 * @author harsh.patel
 *
 */
/*
 *  TODO test case where the primary peer configured can be secondary for some other peer. in that case as well
 *  the request should not be forwarded to that primary peer because it is tried once.
 */
/*
 * TODO Harsh, issue in statefulness when host identity is not resolved and the request is load balanced to the
 * peer whose host identity may be null. What should we do about those scenarios?
 */
public class DefaultDiameterPeerGroup implements ESCommunicatorGroup<DiameterPeerCommunicator>, DiameterPeerCommGroup {

	private static final String MODULE = "DIA-PEER-COMM-GRP";
	private static final String PARAM_FAILED_GROUP_NAMES = "PARAM_FAILED_GROUP_NAMES";
	private final Map<String, DiameterPeerCommunicator> communicators;
	private final String name;
	private final long transactionTimeout;
	private TimeSource timesource;
	private boolean statefull;
	private IStackContext stackContext;
	
	private LoadBalancer<DiameterPeerCommunicator> roundRobinLoadBalancer = 
			new RoundRobinLoadBalancer<DiameterPeerCommunicator>(true);
	
	public DefaultDiameterPeerGroup(IStackContext stackContext, 
			String name,
			int maxRetry,
			long transactionTimeout,
			boolean statefull) {
		this(stackContext, name, maxRetry, transactionTimeout, TimeSource.systemTimeSource(), statefull);
	}
	
	@VisibleForTesting
	public DefaultDiameterPeerGroup(IStackContext stackContext, 
			String name,
			int maxRetry,
			long transactionTimeout,
			TimeSource timesource,
			boolean statefull) {
		this.stackContext = stackContext;
		this.name = name;
		this.transactionTimeout = transactionTimeout;
		this.timesource = timesource;
		this.communicators = new ConcurrentHashMap<String, DiameterPeerCommunicator>(10, 0.75f, 4);
		this.statefull = statefull;
	}
	
	@Override
	public void addCommunicator(DiameterPeerCommunicator diameterCommunicator, int weightage) {
		String secondaryPeerName = stackContext.getPeerData(diameterCommunicator.getName()).getSecondaryPeerName();
		if (secondaryPeerName != null) {
			DiameterPeerCommunicator haCommunicator = asHACommunicator(diameterCommunicator, secondaryPeerName);
			if (diameterCommunicator.getHostIdentity() != null) {
				this.communicators.put(diameterCommunicator.getHostIdentity(), haCommunicator);
			} else {
				diameterCommunicator.addESIEventListener(new HostIdentityListener(haCommunicator));
			}
			roundRobinLoadBalancer.addCommunicator(haCommunicator, weightage);
		} else {
			DiameterPeerCommunicator nonHaCommunicator = asNonHACommunicator(diameterCommunicator);
			if (diameterCommunicator.getHostIdentity() != null) {
				this.communicators.put(diameterCommunicator.getHostIdentity(), nonHaCommunicator);
			} else {
				nonHaCommunicator.addESIEventListener(new HostIdentityListener(nonHaCommunicator));
			}
			roundRobinLoadBalancer.addCommunicator(nonHaCommunicator, weightage);
		}
	}

	private DiameterPeerCommunicator asNonHACommunicator(DiameterPeerCommunicator diameterCommunicator) {
		if (statefull) {
			DiameterPeerCommunicator statefullCommunicator = new StatefullNonHighAvailabilityCommunicator(diameterCommunicator);
			this.communicators.put(statefullCommunicator.getName(), statefullCommunicator);
			LogManager.getLogger().debug(MODULE, "Statefull non high availability communicator created: " + statefullCommunicator.getName());
			return statefullCommunicator;
		} else {
			DiameterPeerCommunicator statelessCommunicator = new StatelessNonHighAvailabilityCommunicator(diameterCommunicator);
			this.communicators.put(statelessCommunicator.getName(), statelessCommunicator);
			LogManager.getLogger().debug(MODULE, "Stateless non high availability communicator created: " + statelessCommunicator.getName());
			return statelessCommunicator;
		}
	}

	@Override
	public void addCommunicator(DiameterPeerCommunicator diameterCommunicator) {
		addCommunicator(diameterCommunicator, LoadBalancer.DEFAULT_WEIGHT);
	}

	@Override
	public boolean isAlive() {
		return roundRobinLoadBalancer.isAlive();
	}

	@Override
	public void sendClientInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest, ResponseListener listener) throws CommunicationException {
		if (statefull) {
			@Nullable HighAvailabilityStatus haStatus = (HighAvailabilityStatus) session.getParameter(getName());
			if (haStatus == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Statefull info not found in session, request will be load balanced.");
				}
				DiameterPeerCommunicator communicator = loadBalanceRequest(session, diameterRequest, listener);
				if (communicator == null) {
					throw new CommunicationException("No alive peer found in group: " + getName());
				}
			} else {
				DiameterPeerCommunicator statefullCommunicator = getCommunicator(haStatus.getPrimaryPeer());
				if (LogManager.getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Statefull info found in session, selected statefull peer: " + statefullCommunicator.getHostIdentity());
				}
				statefullCommunicator.sendClientInitiatedRequest(session, diameterRequest, listener);
			}
		} else {
			DiameterPeerCommunicator communicator = loadBalanceRequest(session, diameterRequest, listener);
			if (communicator == null) {
				throw new CommunicationException("No alive peer found in group: " + getName());
			}
		}
	}
	
	private DiameterPeerCommunicator loadBalanceRequest(DiameterSession session, DiameterRequest diameterRequest, ResponseListener listener) {
		if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
			LogManager.getLogger().trace(MODULE, "Load balancing request.");
		}
		
		DiameterPeerCommunicator communicator;
		while ((communicator = selectPeer(diameterRequest)) != null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Selected peer: " + communicator.getHostIdentity()
						+ " by load balancing.");
			}
			try {
				communicator.sendClientInitiatedRequest(session, diameterRequest, listener);
				break;
			} catch (CommunicationException ex) {
				addFailedGroup(communicator, diameterRequest);
				if (getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Error while sending diameter packet: "
							+ diameterRequest.getSessionID() 
							+ " to peer: "+ communicator.getHostIdentity() +". Reason: " + ex.getMessage()
							+ ", trying to send to another peer");
				getLogger().trace(MODULE, ex);
			}
		}
		if (communicator == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Exhausted peer group: " + getName() + ", no peer left to try.");
			}
		}
		return communicator;
	}

	@SuppressWarnings("unchecked")
	private void addFailedGroup(DiameterPeerCommunicator peerCommunicatorGroup, DiameterRequest diameterRequest) {
		List<String> failedHAGroups = (List<String>) diameterRequest.getParameter(PARAM_FAILED_GROUP_NAMES);
		if (failedHAGroups == null) {
			failedHAGroups = new ArrayList<String>(1);
			diameterRequest.setParameter(PARAM_FAILED_GROUP_NAMES, failedHAGroups);
		}
		failedHAGroups.add(peerCommunicatorGroup.getName());
		if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
			LogManager.getLogger().trace(MODULE, "Failed group: " + peerCommunicatorGroup.getName() + " added in request.");
		}
	}

	private boolean isInitialTry(DiameterRequest diameterRequest) {
		return diameterRequest.getParameter(PARAM_FAILED_GROUP_NAMES) == null;
	}

	@SuppressWarnings("unchecked")
	private DiameterPeerCommunicator selectPeer(DiameterRequest diameterRequest)  {
		DiameterPeerCommunicator[] ignoredCommunicators = null;
		if (isInitialTry(diameterRequest) == false) {
			
			List<String> failedHAGroups = (List<String>) diameterRequest.getParameter(PARAM_FAILED_GROUP_NAMES);
			ignoredCommunicators = new DiameterPeerCommunicator[failedHAGroups.size()];
			for (int i = 0; i < failedHAGroups.size(); i++) {
				ignoredCommunicators[i] = getCommunicator(failedHAGroups.get(i));
			}
		}

		DiameterPeerCommunicator haCommunicator = null;
		if (ignoredCommunicators != null) {
			haCommunicator = roundRobinLoadBalancer.getSecondaryCommunicator(ignoredCommunicators);
		} else {
			haCommunicator = roundRobinLoadBalancer.getCommunicator();
		}

		return haCommunicator;
	}

	private DiameterPeerCommunicator getCommunicator(String peerNameOrHostIdentity) {
		return communicators.get(peerNameOrHostIdentity);
	}

	@Override
	public String getName() {
		return name;
	}
	
	private DiameterPeerCommunicator asHACommunicator(final DiameterPeerCommunicator diameterCommunicator, String secondaryPeerName) {
		DiameterPeerCommunicator secondaryCommunicator = stackContext.getPeerCommunicator(secondaryPeerName);
		if (secondaryCommunicator == null) {
			LogManager.getLogger().warn(MODULE, "Peer not found for secondary communicator: " + secondaryPeerName + ", peer: "
					+ diameterCommunicator.getName() + " will not work in high availability mode.");
			return asNonHACommunicator(diameterCommunicator);
		}
		
		if (statefull) {
			DiameterPeerCommunicator soCommunicator = new SwitchOverCommunicator(diameterCommunicator,
				secondaryCommunicator);
			this.communicators.put(soCommunicator.getName(), soCommunicator);
			this.communicators.put(diameterCommunicator.getName(), soCommunicator);
			LogManager.getLogger().debug(MODULE, "Switch over communicator created: " + soCommunicator.getName());
			return soCommunicator;
		} else {
			DiameterPeerCommunicator foCommunicator = new FailoverCommunicator(diameterCommunicator,
					secondaryCommunicator);
			this.communicators.put(foCommunicator.getName(), foCommunicator);
			this.communicators.put(diameterCommunicator.getName(), foCommunicator);
			LogManager.getLogger().debug(MODULE, "Fail over communicator created: " + foCommunicator.getName());
			return foCommunicator;
		}
	}

	private class HostIdentityListener implements ESIEventListener<ESCommunicator> {
		private DiameterPeerCommunicator diameterPeerCommunicator;

		private HostIdentityListener(DiameterPeerCommunicator diameterPeerCommunicator) {
			this.diameterPeerCommunicator = diameterPeerCommunicator;
		}

		@Override
		public void alive(ESCommunicator esCommunicator) {
			DiameterPeerCommunicator peerCommunicator = (DiameterPeerCommunicator) esCommunicator;
			if (peerCommunicator.getHostIdentity() != null) {
				communicators.put(peerCommunicator.getHostIdentity(), diameterPeerCommunicator);
				peerCommunicator.removeESIEventListener(this);
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
					LogManager.getLogger().trace(MODULE, "Peer: " + esCommunicator.getName()
							+ " is alive and host identity is: " + peerCommunicator.getHostIdentity());
				}
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
					LogManager.getLogger().trace(MODULE, "Peer: " + esCommunicator.getName()
							+ " is alive and host identity is not known.");
				}
			}
		}

		@Override
		public void dead(ESCommunicator esCommunicator) {
				
		}
	}
	
	private boolean isTransactionTimeout(DiameterRequest diameterRequest) {
		if (isInitialTry(diameterRequest) == false) {
			if (transactionTimeout > 0) {
				long currentTimeMillis = timesource.currentTimeInMillis();
				long actualTransactionTime = currentTimeMillis - diameterRequest.creationTimeMillis() ;
				if (transactionTimeout <= actualTransactionTime) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * A stateless communicator that always sends request to primary communicator and in case
	 * of timeout or retryable result code from primary, fails over to failover/secondary communicator.
	 * 
	 * @author narendra.pathai
	 *
	 */
	private class FailoverCommunicator implements DiameterPeerCommunicator {
		private static final String MODULE = "FAILOVER-COMMUNICATOR";
		
		private final DiameterPeerCommunicator primaryCommunicator;
		private final DiameterPeerCommunicator secondaryCommunicator;
		private final List<ESIEventListener<ESCommunicator>> eventListeners = 
				new CopyOnWriteArrayList<ESIEventListener<ESCommunicator>>();
		private volatile boolean alive = true;
		
		public FailoverCommunicator(@Nonnull DiameterPeerCommunicator primaryCommunicator,
				@Nonnull DiameterPeerCommunicator secondaryCommunicator) {
			this.primaryCommunicator = primaryCommunicator;
			this.secondaryCommunicator = secondaryCommunicator;
			EventListenerImpl listener = new EventListenerImpl();
			this.primaryCommunicator.addESIEventListener(listener);
			this.secondaryCommunicator.addESIEventListener(listener);
		}
		
		@Override
		public void reInit() throws InitializationFailedException {
			
		}

		@Override
		public void init() throws InitializationFailedException {
			
		}

		@Override
		public boolean isAlive() {
			return alive;
		}

		@Override
		public void scan() {
			
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
			
		}

		@Override
		public String getName() {
			return "P = " + primaryCommunicator.getName() + "- S = " + secondaryCommunicator.getName();
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
			
		}
		
		private class EventListenerImpl implements ESIEventListener<ESCommunicator> {
			
			@Override
			public synchronized void alive(ESCommunicator esCommunicator) {
				if (isAlive()) {
					return;
				}

				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, getName() + " fail over communicator is alive.");
				}
				
				alive = true;
				for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
					eventListner.alive(FailoverCommunicator.this);
				}
			}

			@Override
			public synchronized void dead(ESCommunicator esCommunicator) {
				if (primaryCommunicator.isAlive() == false && secondaryCommunicator.isAlive() == false) {
					
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, getName() + " fail over communicator is dead.");
					}
					
					alive = false;
					for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
						eventListner.dead(FailoverCommunicator.this);
					}
				}
			}
		}

		@Override
		/*
		 *  TODO verify failed peer lists after all test cases.
		 */
		public void sendClientInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest, ResponseListener listener) throws CommunicationException {
			sendStatelessRequest(diameterRequest, listener, session);
		}

		private void sendStatelessRequest(DiameterRequest diameterRequest, ResponseListener listener,
				DiameterSession session) throws CommunicationException {
			if (primaryCommunicator.isAlive()) {
				try {
					trySendClientInitiatedRequest(primaryCommunicator, session, diameterRequest, new FailoverOrLoadBalanceOnFailureListener(diameterRequest, listener));
				} catch (CommunicationException ex) { 
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Unable to send request to primary communicator: " + primaryCommunicator.getHostIdentity()
								+ ", Reason: " + ex.getMessage() + ". Trying secondary communicator.");
					}
					ignoreTrace(ex);
					trySecondary(diameterRequest, listener, session);
				}
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Primary communicator: " + primaryCommunicator.getHostIdentity()
							+ " is not alive, trying secondary communicator.");
				}
				trySecondary(diameterRequest, listener, session);
			}
		}

		private void trySecondary(DiameterRequest diameterRequest, ResponseListener listener, DiameterSession session)
				throws CommunicationException {
			if (secondaryCommunicator.isAlive() == false) {
				throw new CommunicationException("No peer is alive in HA group: " + getHostIdentity());
			}
			if (isAlreadyTried(secondaryCommunicator, diameterRequest)) {
				throw new CommunicationException(alreadyTriedMessage(secondaryCommunicator));
			}
			trySendClientInitiatedRequest(secondaryCommunicator, session, diameterRequest, new LoadBalanceOnFailureListener(diameterRequest, listener));
		}
		
		@Override
		public String getHostIdentity() {
			return primaryCommunicator.getHostIdentity() + "-" + secondaryCommunicator.getHostIdentity();
		}
		
		private class FailoverOrLoadBalanceOnFailureListener implements ResponseListener {

			private DiameterRequest diameterRequest;
			private ResponseListener listener;

			public FailoverOrLoadBalanceOnFailureListener(DiameterRequest diameterRequest,
					ResponseListener listener) {
				this.diameterRequest = diameterRequest;
				this.listener = listener;
			}

			@Override
			public void requestTimedout(String hostIdentity, DiameterSession session) {
				logRequestTimedout(hostIdentity);
				diameterRequest.addFailedPeer(hostIdentity);
				
				if (isTransactionTimeout(diameterRequest)) {
					getLogger().info(MODULE, transactionTimedoutMessage());
					
					listener.requestTimedout(hostIdentity, session);
				} else {
					DiameterPeerCommunicator communicator = tryFailoverOrLoadBalance(session);
					if (communicator == null) {
						listener.requestTimedout(hostIdentity, session);
					}
				}
			}

			@Override
			public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity
					, DiameterSession session) {
				RetryableResultCode retryableResultCode = new RetryableResultCode(diameterAnswer);

				if (retryableResultCode.isRetryable()) {
					diameterRequest.addFailedPeer(hostIdentity);
					
					if (isTransactionTimeout(diameterRequest)) {
						getLogger().info(MODULE, transactionTimedoutMessage());
						listener.responseReceived(diameterAnswer, hostIdentity, session);
						return;
					}
					
					logRequestRetryDueToRetryableResultCode(hostIdentity, retryableResultCode);
					
					DiameterPeerCommunicator communicator = tryFailoverOrLoadBalance(session);
					if (communicator == null) {
						listener.responseReceived(diameterAnswer, hostIdentity, session);
					}
				} else {
					listener.responseReceived(diameterAnswer, hostIdentity, session);
				}
			}

			private DiameterPeerCommunicator tryFailoverOrLoadBalance(DiameterSession session) {
				if (isAlreadyTried(secondaryCommunicator, diameterRequest)) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, alreadyTriedMessage(secondaryCommunicator));
					}
					addFailedGroup(FailoverCommunicator.this, diameterRequest);
					
					return loadBalanceRequest(session, diameterRequest, listener);
				}
				
				try {
					trySendClientInitiatedRequest(secondaryCommunicator, session, diameterRequest, new LoadBalanceOnFailureListener(diameterRequest, listener));
					return FailoverCommunicator.this;
				} catch (CommunicationException e) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Unable to send request to secondary peer for HA group: " + getHostIdentity()
								+ ", Reason: " + e.getMessage());
					}
					addFailedGroup(FailoverCommunicator.this, diameterRequest);
					
					return loadBalanceRequest(session, diameterRequest, listener);
				}
			}
		}
		
		private class LoadBalanceOnFailureListener implements ResponseListener {

			private final DiameterRequest diameterRequest;
			private final ResponseListener listener;

			public LoadBalanceOnFailureListener(DiameterRequest diameterRequest,
					ResponseListener listener) {
				this.diameterRequest = diameterRequest;
				this.listener = listener;
			}

			@Override
			public void requestTimedout(String hostIdentity, DiameterSession session) {
				logRequestTimedout(hostIdentity);
				diameterRequest.addFailedPeer(hostIdentity);
				addFailedGroup(FailoverCommunicator.this, diameterRequest);
				
				if (isTransactionTimeout(diameterRequest)) {
					getLogger().info(MODULE, transactionTimedoutMessage());
					listener.requestTimedout(hostIdentity, session);
				} else {
					DiameterPeerCommunicator communicator = loadBalanceRequest(session, diameterRequest, listener);
					if (communicator == null) {
						listener.requestTimedout(hostIdentity, session);
					}
				}
			}

			@Override
			public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
				RetryableResultCode retryableResultCode = new RetryableResultCode(diameterAnswer);
				
				if (retryableResultCode.isRetryable()) {

					diameterRequest.addFailedPeer(hostIdentity);
					addFailedGroup(FailoverCommunicator.this, diameterRequest);

					if (isTransactionTimeout(diameterRequest)) {
						getLogger().info(MODULE, transactionTimedoutMessage());
						listener.responseReceived(diameterAnswer, hostIdentity, session);
						return;
					}
					
					logRequestRetryDueToRetryableResultCode(hostIdentity, retryableResultCode);

					DiameterPeerCommunicator diameterPeerCommunicator = loadBalanceRequest(session, diameterRequest, listener);
					if (diameterPeerCommunicator == null) {
						listener.responseReceived(diameterAnswer, hostIdentity, session);
					}
				} else {
					listener.responseReceived(diameterAnswer, hostIdentity, session);
				}
			}
		}
		
		@Override
		public void sendServerInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest,
				ResponseListener listener) throws CommunicationException {
			
		}

		@Override
		public void sendAnswer(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer)
				throws CommunicationException {
			
		}
	}
	
	/**
	 * A statefull communicator that uses stored information of active peer in session to maintain
	 * session statefullness. The active peer is tried first and then the standby peer is tried. Any
	 * of the primary or secondary peer can take the role of active peer. The active peer is
	 * successful answerer of last request for a session.
	 * <br/><br/>
	 * In case when the request is for a new session then it behaves same as a {@link FailoverCommunicator}.
	 * 
	 * @author narendra.pathai
	 *
	 */
	private class SwitchOverCommunicator implements DiameterPeerCommunicator {
		private static final String MODULE = "HA-COMMUNICATOR";
		
		private final FailoverCommunicator failoverCommunicator;
		private final DiameterPeerCommunicator primaryCommunicator;
		private final DiameterPeerCommunicator secondaryCommunicator;
		private final List<ESIEventListener<ESCommunicator>> eventListeners = 
				new CopyOnWriteArrayList<ESIEventListener<ESCommunicator>>();
		
		public SwitchOverCommunicator(@Nonnull DiameterPeerCommunicator primaryCommunicator,
				@Nonnull DiameterPeerCommunicator secondaryCommunicator) {
			this.primaryCommunicator = primaryCommunicator;
			this.secondaryCommunicator = secondaryCommunicator;
			failoverCommunicator = new FailoverCommunicator(primaryCommunicator, secondaryCommunicator);
			failoverCommunicator.addESIEventListener(new ESIEventListener<ESCommunicator>() {

				@Override
				public void alive(ESCommunicator esCommunicator) {
					for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
						eventListner.alive(SwitchOverCommunicator.this);
					}
				}

				@Override
				public void dead(ESCommunicator esCommunicator) {
					for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
						eventListner.dead(SwitchOverCommunicator.this);
					}
				}
			});
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
		/*
		 *  TODO verify failed peer lists after all test cases.
		 */
		public void sendClientInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest, ResponseListener listener) throws CommunicationException {
			HighAvailabilityStatus haStatus = (HighAvailabilityStatus) session.getParameter(DefaultDiameterPeerGroup.this.getName());
			if (haStatus == null) {
				failoverCommunicator.sendClientInitiatedRequest(session, diameterRequest, new SaveHighAvailabilityStatus(primaryCommunicator, secondaryCommunicator, listener));
			} else {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "High availability status: " 
							+ haStatus + " found in session.");
				}
				
				DiameterPeerCommunicator activeCommunicator;
				DiameterPeerCommunicator standbyCommunicator;
				
				if (primaryCommunicator.getHostIdentity().equals(haStatus.getActivePeer())) {
					activeCommunicator = primaryCommunicator;
					standbyCommunicator = secondaryCommunicator;
				} else {
					activeCommunicator = secondaryCommunicator;
					standbyCommunicator = primaryCommunicator;
				}
				
				
				try {
					trySendClientInitiatedRequest(activeCommunicator, session, diameterRequest, new SwitchOverListener(standbyCommunicator, listener, diameterRequest, haStatus));
				} catch (CommunicationException ex) {
					LogManager.getLogger().info(MODULE, "Unable to send diameter request to active peer: " + activeCommunicator.getHostIdentity()
							+ ", Reason: " + ex.getMessage() + ", trying standby peer: " + standbyCommunicator.getHostIdentity());
					
					try {
						trySendClientInitiatedRequest(standbyCommunicator, session, diameterRequest, new StandbyResponseListener(diameterRequest, listener, haStatus));
					} catch (CommunicationException e) {
						if (LogManager.getLogger().isInfoLogLevel()) {
							LogManager.getLogger().info(MODULE, "Unable to send request to standby peer: " + standbyCommunicator.getHostIdentity()
									+ ", Reason: " + e.getMessage());
						}
						addFailedGroup(SwitchOverCommunicator.this, diameterRequest);
						
						throw new CommunicationException("HA group: " + getHostIdentity() + " is dead.");
					}
				}
			}
		}

		@Override
		public String getHostIdentity() {
			return primaryCommunicator.getHostIdentity() + "-" + secondaryCommunicator.getHostIdentity();
		}
		
		private final class StandbyResponseListener implements ResponseListener {
			
			private HighAvailabilityStatus haStatus;
			private DiameterRequest diameterRequest;
			private ResponseListener listener;

			public StandbyResponseListener(DiameterRequest diameterRequest, ResponseListener listener,
					HighAvailabilityStatus haStatus) {
				this.diameterRequest = diameterRequest;
				this.listener = listener;
				this.haStatus = haStatus;
			}

			@Override
			public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
				RetryableResultCode retryableResultCode = new RetryableResultCode(diameterAnswer);

				if (retryableResultCode.isRetryable() == false) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Updated active peer in high availability status. Old active peer: "
								+ haStatus.getActivePeer() + ", new active peer: " + hostIdentity);
					}
					haStatus.setActivePeer(hostIdentity);
				} else {
					diameterRequest.addFailedPeer(hostIdentity);
					addFailedGroup(SwitchOverCommunicator.this, diameterRequest);
				}
				
				listener.responseReceived(diameterAnswer, hostIdentity, session);
			}

			@Override
			public void requestTimedout(String hostIdentity, DiameterSession session) {
				diameterRequest.addFailedPeer(hostIdentity);
				logRequestTimedout(hostIdentity);
				
				listener.requestTimedout(hostIdentity, session);
			}
		}
		
		private class SwitchOverListener implements ResponseListener {

			private DiameterPeerCommunicator standbyCommunicator;
			private ResponseListener listener;
			private DiameterRequest diameterRequest;
			private HighAvailabilityStatus haStatus;

			public SwitchOverListener(DiameterPeerCommunicator standbyCommunicator, 
					ResponseListener listener,
					DiameterRequest diameterRequest,
					HighAvailabilityStatus haStatus) {
				this.standbyCommunicator = standbyCommunicator;
				this.listener = listener;
				this.diameterRequest = diameterRequest;
				this.haStatus = haStatus;
			}

			@Override
			public void requestTimedout(String hostIdentity, DiameterSession session) {
				diameterRequest.addFailedPeer(hostIdentity);
				logRequestTimedout(hostIdentity);
				
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Trying standby peer: " + standbyCommunicator.getHostIdentity());
				}

				try {
					trySendClientInitiatedRequest(standbyCommunicator, session, diameterRequest, new StandbyResponseListener(diameterRequest, listener, haStatus));
				} catch (CommunicationException e) { 
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Unable to send request to standby peer: " + standbyCommunicator.getHostIdentity()
								+ ", Reason: " + e.getMessage());
					}
					ignoreTrace(e);
					addFailedGroup(SwitchOverCommunicator.this, diameterRequest);
					
					listener.requestTimedout(hostIdentity, session);
				}
			}

			@Override
			public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
				RetryableResultCode retryableResultCode = new RetryableResultCode(diameterAnswer);

				if (retryableResultCode.isRetryable()) {
					diameterRequest.addFailedPeer(hostIdentity);
					
					logRequestRetryDueToRetryableResultCode(hostIdentity, retryableResultCode);
					
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Trying standby peer: " + standbyCommunicator.getHostIdentity());
					}
					
					try {
						trySendClientInitiatedRequest(standbyCommunicator, session, diameterRequest, new StandbyResponseListener(diameterRequest, listener, haStatus));
					} catch (CommunicationException e) {
						if (LogManager.getLogger().isInfoLogLevel()) {
							LogManager.getLogger().info(MODULE, "Unable to send request to standby peer: " + standbyCommunicator.getHostIdentity()
									+ ", Reason: " + e.getMessage());
						}
						addFailedGroup(SwitchOverCommunicator.this, diameterRequest);
						LogManager.getLogger().trace(MODULE, e);
						listener.responseReceived(diameterAnswer, hostIdentity, session);
					}
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Updated active peer in high availability status. Old active peer: "
								+ haStatus.getActivePeer() + ", new active peer: " + hostIdentity);
					}
					haStatus.setActivePeer(hostIdentity);
					listener.responseReceived(diameterAnswer, hostIdentity, session);
				}
			}
		}

		@Override
		public void sendServerInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest,
				ResponseListener listener) throws CommunicationException {
			
		}

		@Override
		public void sendAnswer(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer)
				throws CommunicationException {
			
		}

		@Override
		public void init() throws InitializationFailedException {
			
		}

		@Override
		public boolean isAlive() {
			return failoverCommunicator.isAlive();
		}

		@Override
		public void scan() {
			
		}

		@Override
		public void stop() {
			
		}

		@Override
		public String getName() {
			return failoverCommunicator.getName();
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
			failoverCommunicator.registerAlertListener(alertListener);
		}

		@Override
		public void reInit() throws InitializationFailedException {
			
		}
	}
	
	
	private class SaveHighAvailabilityStatus implements ResponseListener {
		private ResponseListener listener;
		private DiameterPeerCommunicator primaryCommunicator;
		private DiameterPeerCommunicator secondaryCommunicator;

		public SaveHighAvailabilityStatus(DiameterPeerCommunicator primaryCommunicator,
				DiameterPeerCommunicator secondaryCommunicator,
				ResponseListener listener) {
			this.primaryCommunicator = primaryCommunicator;
			this.secondaryCommunicator = secondaryCommunicator;
			this.listener = listener;
		}

		@Override
		public void requestTimedout(String hostIdentity, DiameterSession session) {
			listener.requestTimedout(hostIdentity, session);
		}

		@Override
		public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
			RetryableResultCode retryableResultCode = new RetryableResultCode(diameterAnswer);
			if (retryableResultCode.isRetryable() == false) {
				HighAvailabilityStatus status = new HighAvailabilityStatus(primaryCommunicator.getHostIdentity(),
						secondaryCommunicator.getHostIdentity(), primaryCommunicator.getHostIdentity());
				session.setParameter(DefaultDiameterPeerGroup.this.getName(), status);
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Saved high availability status: " + status + " in session.");
				}
			}
			
			listener.responseReceived(diameterAnswer, hostIdentity, session);
		}
	}
	
	private class StatelessNonHighAvailabilityCommunicator implements DiameterPeerCommunicator {
		
		private DiameterPeerCommunicator diameterCommunicator;
		private final List<ESIEventListener<ESCommunicator>> eventListeners = 
				new CopyOnWriteArrayList<ESIEventListener<ESCommunicator>>();
		
		public StatelessNonHighAvailabilityCommunicator(DiameterPeerCommunicator diameterCommunicator) {
			this.diameterCommunicator = diameterCommunicator;
			this.diameterCommunicator.addESIEventListener(new ESIEventListenerImpl());
		}

		@Override
		public void init() throws InitializationFailedException {
			
		}

		@Override
		public boolean isAlive() {
			return diameterCommunicator.isAlive();
		}

		@Override
		public void scan() {
			
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
			
		}

		@Override
		public String getName() {
			return diameterCommunicator.getName();
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
			
		}

		@Override
		public void reInit() throws InitializationFailedException {
			
		}

		@Override
		public void sendClientInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest,
				ResponseListener listener) throws CommunicationException {
			trySendClientInitiatedRequest(diameterCommunicator, session, diameterRequest, 
					new LoadBalanceOnFailureListener(listener, diameterRequest));
		}

		@Override
		public void sendServerInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest,
				ResponseListener listener) throws CommunicationException {
			
		}

		@Override
		public void sendAnswer(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer)
				throws CommunicationException {
			
		}

		@Override
		public String getHostIdentity() {
			return diameterCommunicator.getHostIdentity();
		}
		
		private class LoadBalanceOnFailureListener implements ResponseListener {

			@Nonnull private ResponseListener userListener;
			private DiameterRequest diameterRequest;
			
			public LoadBalanceOnFailureListener( 
					@Nonnull ResponseListener listener, DiameterRequest diameterRequest) {
				this.userListener = listener;
				this.diameterRequest = diameterRequest;
			}
			
			@Override
			public void requestTimedout(String hostIdentity, DiameterSession session) {
				diameterRequest.addFailedPeer(hostIdentity);
				logRequestTimedout(hostIdentity);
				addFailedGroup(StatelessNonHighAvailabilityCommunicator.this, diameterRequest);
					
				if (isTransactionTimeout(diameterRequest)) {
					getLogger().info(MODULE, transactionTimedoutMessage());
					userListener.requestTimedout(hostIdentity, session);
				} else {
					DiameterPeerCommunicator communicator = loadBalanceRequest(session, diameterRequest, userListener);
					if (communicator == null) {
						userListener.requestTimedout(hostIdentity, session);
					}
				}
			}

			@Override
			public void responseReceived(DiameterAnswer diameterAnswer,
					String hostIdentity, DiameterSession session) {
				
				RetryableResultCode retryableResultCode = new RetryableResultCode(diameterAnswer);

				if (retryableResultCode.isRetryable()) {

					diameterRequest.addFailedPeer(hostIdentity); 
					addFailedGroup(StatelessNonHighAvailabilityCommunicator.this, diameterRequest);
					
					if (isTransactionTimeout(diameterRequest)) {
						getLogger().info(MODULE, transactionTimedoutMessage());
						userListener.responseReceived(diameterAnswer, hostIdentity, session);
						return;
					}

					logRequestRetryDueToRetryableResultCode(hostIdentity, retryableResultCode);

					DiameterPeerCommunicator diameterPeerCommunicator = loadBalanceRequest(session, diameterRequest, userListener);
					if (diameterPeerCommunicator == null) {
						userListener.responseReceived(diameterAnswer, hostIdentity, session);
					}
				} else {
					userListener.responseReceived(diameterAnswer, hostIdentity, session);
				}
			}
		}
		
		private class ESIEventListenerImpl implements ESIEventListener<ESCommunicator> {

			@Override
			public void alive(ESCommunicator esCommunicator) {
				for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
					eventListner.alive(StatelessNonHighAvailabilityCommunicator.this);
				}
			}

			@Override
			public void dead(ESCommunicator esCommunicator) {
				for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
					eventListner.dead(StatelessNonHighAvailabilityCommunicator.this);
				}
			}
		}
	}
	
	private class StatefullNonHighAvailabilityCommunicator implements DiameterPeerCommunicator {
		private static final String MODULE = "NON-HA-COMMUNICATOR";
		
		private StatelessNonHighAvailabilityCommunicator statelessCommunicator;
		private final List<ESIEventListener<ESCommunicator>> eventListeners = 
				new CopyOnWriteArrayList<ESIEventListener<ESCommunicator>>();
		private DiameterPeerCommunicator diameterCommunicator;
		
		public StatefullNonHighAvailabilityCommunicator(DiameterPeerCommunicator diameterCommunicator) {
			this.diameterCommunicator = diameterCommunicator;
			this.statelessCommunicator = new StatelessNonHighAvailabilityCommunicator(diameterCommunicator);
			this.statelessCommunicator.addESIEventListener(new ESIEventListenerImpl());
		}

		@Override
		public void init() throws InitializationFailedException {
			
		}

		@Override
		public boolean isAlive() {
			return statelessCommunicator.isAlive();
		}

		@Override
		public void scan() {
			
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
			
		}

		@Override
		public String getName() {
			return statelessCommunicator.getName();
		}

		@Override
		public String getTypeName() {
			return statelessCommunicator.getTypeName();
		}

		@Override
		public ESIStatistics getStatistics() {
			return null;
		}

		@Override
		public void registerAlertListener(AlertListener alertListener) {
			
		}

		@Override
		public void reInit() throws InitializationFailedException {
			
		}

		@Override
		public void sendClientInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest, ResponseListener listener) throws CommunicationException {
			try {
				HighAvailabilityStatus haStatus = (HighAvailabilityStatus) session.getParameter(DefaultDiameterPeerGroup.this.getName());
				if (haStatus == null) {
					statelessCommunicator.sendClientInitiatedRequest(session, diameterRequest, 
							new SaveHighAvailabilityStatus(statelessCommunicator, statelessCommunicator, listener));
				} else {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "High availability status: " 
								+ haStatus + " found in session.");
					}

					trySendClientInitiatedRequest(diameterCommunicator, session, diameterRequest, 
							new NotifyUserOnFailureListener(listener, diameterRequest));
				}
			} catch (CommunicationException ex) {
				addFailedGroup(this, diameterRequest);
				throw ex;
			}
		}

		@Override
		public String getHostIdentity() {
			return diameterCommunicator.getHostIdentity();
		}
		
		private class ESIEventListenerImpl implements ESIEventListener<ESCommunicator> {

			@Override
			public void alive(ESCommunicator esCommunicator) {
				for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
					eventListner.alive(StatefullNonHighAvailabilityCommunicator.this);
				}
			}

			@Override
			public void dead(ESCommunicator esCommunicator) {
				for (ESIEventListener<ESCommunicator> eventListner : eventListeners) {
					eventListner.dead(StatefullNonHighAvailabilityCommunicator.this);
				}
			}
		}
		
		private class NotifyUserOnFailureListener implements ResponseListener {
			private ResponseListener listener;
			private DiameterRequest diameterRequest;

			public NotifyUserOnFailureListener(ResponseListener listener,
					DiameterRequest diameterRequest) {
				this.listener = listener;
				this.diameterRequest = diameterRequest;
			}

			@Override
			public void requestTimedout(String hostIdentity, DiameterSession session) {
				diameterRequest.addFailedPeer(hostIdentity);
				logRequestTimedout(hostIdentity);
				listener.requestTimedout(hostIdentity, session);
			}

			@Override
			public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
				listener.responseReceived(diameterAnswer, hostIdentity, session);
			}
		}

		@Override
		public void sendServerInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest,
				ResponseListener listener) throws CommunicationException {
			
		}

		@Override
		public void sendAnswer(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer)
				throws CommunicationException {
			
		}
	}
	
	private static boolean isAlreadyTried(DiameterPeerCommunicator communicator, DiameterRequest request) {
		if (Collectionz.isNullOrEmpty(request.getFailedPeerList())) {
			return false;
		}
		return request.getFailedPeerList().contains(communicator.getHostIdentity());
	}
	
	private static String alreadyTriedMessage(DiameterPeerCommunicator communicator) {
		return "Not forwarding diameter request to peer: " + communicator.getHostIdentity()
			+ ", Reason: Already tried.";
	}
	
	private static void trySendClientInitiatedRequest(DiameterPeerCommunicator communicator, DiameterSession session, DiameterRequest request, ResponseListener listener) throws CommunicationException {
		try {
			communicator.sendClientInitiatedRequest(session, request, listener);
		} catch (CommunicationException e) {
			request.addFailedPeer(communicator.getHostIdentity());
			throw e;
		}
	}
	
	private String transactionTimedoutMessage() {
		return "Transaction time exceeds configured max transaction timeout of: " + transactionTimeout;
	}
	

	private void logRequestTimedout(String hostIdentity) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Request to peer: " + hostIdentity + " timed out.");
		}
	}
	
	private void logRequestRetryDueToRetryableResultCode(String hostIdentity,
			ResponseListener.RetryableResultCode retryableResultCode) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Answer with retryable result code: " + retryableResultCode.getResultCode()
					+ " received from peer: " + hostIdentity + ", retrying request.");
		}
	}
}
