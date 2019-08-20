package com.elitecore.diameterapi.core.common.peer;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.common.peers.PeerProvider;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;

/**
 * Created by harsh on 6/22/15.
 */
public class DiameterPeerCommunicatorFactory {

    private static final String MODULE = "DIA-PEER-COMM-FACTORY";
    private final PeerProvider peerProvider;
    private final CommunicatorStore peerCommunicatorStore;
	private final IDiameterStackContext stackContext;


    public DiameterPeerCommunicatorFactory(IDiameterStackContext stackContext, PeerProvider peerProvider) {
        this.stackContext = stackContext;
        this.peerProvider = peerProvider;
        this.peerCommunicatorStore = new CommunicatorStore();
    }

    @Nullable public DiameterPeerCommunicator createInstance(String peerNameOrHostIdentity) {

        IPeerListener peerListener = peerProvider.getPeer(peerNameOrHostIdentity);

        if(peerListener == null) {
            peerListener = peerProvider.getPeerByName(peerNameOrHostIdentity);
        }

        if(peerListener == null) {
            return null;
        }

       return getOrCreateInstance(peerListener);
    }

    private DiameterPeerCommunicator getOrCreateInstance(IPeerListener peerListener) {
    	
        DiameterPeerCommunicator diameterPeerCommunicator = peerCommunicatorStore.get(peerListener);

        if (diameterPeerCommunicator != null) {
            return diameterPeerCommunicator;
        }
        
        synchronized (this) {
        	diameterPeerCommunicator = peerCommunicatorStore.get(peerListener);

            if (diameterPeerCommunicator != null) {
                return diameterPeerCommunicator;
            }

        diameterPeerCommunicator = create(peerListener);
        peerCommunicatorStore.store(peerListener, diameterPeerCommunicator);
        }
        
        return diameterPeerCommunicator;
    }
    
    private DiameterPeerCommunicator create(IPeerListener peerListener) {
    	DiameterPeerCommunicatorImpl diameterPeerCommunicator = new DiameterPeerCommunicatorImpl(peerListener, stackContext);
        diameterPeerCommunicator.init();

        if (LogManager.getLogger().isDebugLogLevel()) {
        	LogManager.getLogger().debug(MODULE, "Created new Peer communicator for peer: " + peerListener.getPeerName());
        }
        
        String failoverPeerName = peerListener.getPeerData().getSecondaryPeerName();
        if (failoverPeerName == null) {
     	   LogManager.getLogger().info(MODULE, "No failover peer configured for peer: " + peerListener.getHostIdentity());
     	   return diameterPeerCommunicator;
        }
        
        IPeerListener failoverPeerListener = peerProvider.getPeerByName(failoverPeerName);
        if (failoverPeerListener == null) {
     	   LogManager.getLogger().warn(MODULE, "Secondary peer: " + failoverPeerName + " attached with peer: " + peerListener.getHostIdentity() + " not found. "
     	   		+ "High availability will not work.");
     	   return diameterPeerCommunicator;
        }
        
        diameterPeerCommunicator.setFailoverPeerListener(failoverPeerListener);
        return diameterPeerCommunicator;
    }

    private class DiameterPeerCommunicatorImpl extends ESCommunicatorImpl implements DiameterPeerCommunicator, DiameterPeerStatusListener {

        private final IPeerListener peerListener;
		private IDiameterStackContext stackContext;
		private IPeerListener failoverPeerListener;

        public DiameterPeerCommunicatorImpl(IPeerListener peerListener, IDiameterStackContext stackContext) {
            super(stackContext.getTaskScheduler());
            this.peerListener = peerListener;
			this.stackContext = stackContext;
        }

        public void setFailoverPeerListener(IPeerListener failoverPeerListener) {
			this.failoverPeerListener = failoverPeerListener;
        }

		public void init() {
            DiameterPeerState diameterPeerState = this.peerListener.registerStatusListener(this);

            if(diameterPeerState == DiameterPeerState.R_Open || diameterPeerState == DiameterPeerState.I_Open) {
                markAlive();
            } else {
                markClosed();
            }
        }
		
		@Override
		public void sendClientInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest,
				ResponseListener listener) throws CommunicationException {
			if (isAlive() == false) {
        		throw new CommunicationException("Unable to send diameter request. Reason: " 
						+ getName() +" not live");
        	}
			
			send(peerListener, session, diameterRequest, listener);
		}
		
		@Override
		public void sendServerInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest,
				ResponseListener listener) throws CommunicationException {
			if (failoverPeerListener == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "No failover communicator attached with peer: " + peerListener.getHostIdentity()
							+ ", server initiated request failover will not occur.");
				}
				try {
					send(peerListener, session, diameterRequest, listener);
	            } catch (CommunicationException ex) {
	            	throw  new CommunicationException(ex);
	            }
				
			} else {
				try {
					send(peerListener, session, diameterRequest, new ServerInitiatedRequestFailoverListener(diameterRequest, listener));
				} catch (CommunicationException ex) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Failed to communicate with peer: " + peerListener.getHostIdentity()
						+ " Reason: " + ex.getMessage() + ", trying failover communicator: " + failoverPeerListener.getHostIdentity());
					}
					send(failoverPeerListener, session, diameterRequest, listener);
	            }
			}
		}

        private void send(IPeerListener peerListener, DiameterSession session, DiameterRequest diameterRequest, @Nonnull ResponseListener listener) throws CommunicationException {
        	try {
                peerListener.sendDiameterRequest(diameterRequest, listener);
                stackContext.updateRealmOutputStatistics(diameterRequest, peerListener.getRealm(), diameterRequest.getRoutingAction());
            } catch (UnhandledTransitionException ex) {
            	diameterRequest.addFailedPeer(peerListener.getHostIdentity());
            	throw new CommunicationException(ex);
            }
        }

        @Override
        public void sendAnswer(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) throws CommunicationException {
        	if (isAlive() == false) {
        		throw new CommunicationException("Unable to send diameter answer. Reason: " 
						+ getName() +" not live");
        	}
        	
        	try {
        		peerListener.sendDiameterAnswer(diameterAnswer);  
        		stackContext.updateRealmOutputStatistics(diameterAnswer, peerListener.getRealm(), diameterRequest.getRoutingAction());
        	}catch (UnhandledTransitionException ex) {
                throw  new CommunicationException(ex);
            }
        }

        @Override
        public String getHostIdentity() {
            return peerListener.getHostIdentity();
        }

        @Override
        protected int getStatusCheckDuration() {
            return ESCommunicatorImpl.NO_SCANNER_THREAD;
        }

        @Override
        public void scan() {
        	// Scan is never called.
        }

        @Override
        public String getName() {
            return peerListener.getPeerName();
        }

        @Override
        public String getTypeName() {
            return "PEER-COMM";
        }

        @Override
        public void markOpen() {
            markAlive();
        }

        @Override
        public void markClosed() {
            markDead();
        }
        
        private class ServerInitiatedRequestFailoverListener implements ResponseListener {

			private DiameterRequest diameterRequest;
			private ResponseListener listener;

			public ServerInitiatedRequestFailoverListener(DiameterRequest diameterRequest,
					ResponseListener listener) {
				this.diameterRequest = diameterRequest;
				this.listener = listener;
			}

			@Override
			public void requestTimedout(String hostIdentity, DiameterSession session) {
				diameterRequest.addFailedPeer(hostIdentity);
				
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Request timedout, trying failover communicator: " + failoverPeerListener.getHostIdentity());
				}
				
				try {
					send(failoverPeerListener, session, diameterRequest, listener);
				} catch (CommunicationException e) { 
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Unable to send request to failover communicator: " + failoverPeerListener.getHostIdentity()
								+ ", Reason: " + e.getMessage());
					}

					ignoreTrace(e);
					listener.requestTimedout(hostIdentity, session);
				}
			}

			@Override
			public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
				RetryableResultCode retryableResultCode = new RetryableResultCode(diameterAnswer);
				if (retryableResultCode.isRetryable()) {
					diameterRequest.addFailedPeer(hostIdentity);
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Retryable result code: " + retryableResultCode.getResultCode() + 
								" received, trying failover communicator: " + failoverPeerListener.getHostIdentity());
					}
					try {
						send(failoverPeerListener, session, diameterRequest, listener);
					} catch (CommunicationException e) { 
						if (LogManager.getLogger().isInfoLogLevel()) {
							LogManager.getLogger().info(MODULE, "Unable to send request to failover communicator: " + failoverPeerListener.getHostIdentity()
									+ ", Reason: " + e.getMessage());
						}

						ignoreTrace(e);
						listener.responseReceived(diameterAnswer, hostIdentity, session);
					}
				} else {
					listener.responseReceived(diameterAnswer, hostIdentity,session);
				}
			}
        }
    }


    private static class CommunicatorStore {

        private final Map<IPeerListener, DiameterPeerCommunicator> diameterPeerCommunicators;
        private final Lock readLock;
        private final Lock writeLock;

        public CommunicatorStore() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            readLock = lock.readLock();
            writeLock = lock.writeLock();
            diameterPeerCommunicators = new IdentityHashMap<IPeerListener, DiameterPeerCommunicator>();
        }

        private DiameterPeerCommunicator get(IPeerListener parameter){
            readLock.lock();
            try {
                return diameterPeerCommunicators.get(parameter);
            }finally {
                readLock.unlock();
            }

        }

        private void store(IPeerListener peer, DiameterPeerCommunicator communicator) {
            writeLock.lock();
            try{
                diameterPeerCommunicators.put(peer, communicator);
            }finally {
                writeLock.unlock();
            }

        }
    }
}
