package com.elitecore.netvertex.gateway.diameter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.core.commons.tls.EliteSSLContextFactory;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.serverx.internal.tasks.CallableSingleExecutionAsyncTask;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException.ListenerRegFailResultCode;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.transport.INetworkConnector;
import com.elitecore.diameterapi.core.common.transport.VirtualConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.PacketProcess;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;

public class DummyStackContext implements IDiameterStackContext{

	private int sessionId = 1;
	private Map<String , Session> sessions = new HashMap<String, Session>();
	private Map<String , PeerData> peerDatas = new HashMap<String, PeerData>();
	private INetworkConnector networkConnector;
	private Set<ApplicationEnum> applicationIdentifiers = new HashSet<ApplicationEnum>();
	
	public void addSession(Session session) { sessions.put(session.getSessionId(), session); }

	@Override
	public INetworkConnector getNetworkConnector(TransportProtocols transportProtocol) { return networkConnector; }
	
	public void setNetworkConnector(INetworkConnector iNetworkConnector) { this.networkConnector = iNetworkConnector; }

	@Override
	public void finalPreResponseProcess(DiameterPacket packet) {}

	@Override
	public PeerData getPeerData(String hostIdentity) { return peerDatas.get(hostIdentity); }
	
	public void addPeerData(PeerData peerData) { peerDatas.put(peerData.getHostIdentity(), peerData); }

	@Override
	public long getNextPeerSequence(String hostIdentity) { return 0; }

	@Override
	public long getNextServerSequence() { return 0; }

	@Override
	public String getNextSessionID() { return Integer.toString(sessionId++); }

	@Override
	public String getNextSessionID(String optionalVal) {return sessionId++ + ";" + optionalVal; }

	@Override
	public Set<ApplicationEnum> getApplicationsIdentifiersList() { return applicationIdentifiers; }
	
	public void addApplicationsIdentifiersList(ApplicationEnum diameterApplicationIdentifier) { this.applicationIdentifiers.add(diameterApplicationIdentifier); }
	
	

	@Override
	public boolean validate() { return false; }

	@Override
	public boolean isNAIEnabled() {return false; }

	@Override
	public boolean isValidNAIRealm(String realm) { return false; }

	@Override
	public DiameterPeerState registerPeerStatusListener(String hostIdentity, DiameterPeerStatusListener listener) throws StatusListenerRegistrationFailException {
		throw new StatusListenerRegistrationFailException(ListenerRegFailResultCode.UNKNOWN); 
	}

	@Override
	public ScheduledFuture<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
		return null;
	}

	@Override
	public <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(
			CallableSingleExecutionAsyncTask<T> task) {
		return null;
	}

	@Override
	public Session generateSession(String destPeer, long appId) {
		return null;
	}

	@Override
	public EliteSSLContextFactory getEliteSSLContextFactory() {
		return null;
	}

	@Override
	public void updateInputStatistics(DiameterPacket packet, String hostIdentity) {
		
	}

	@Override
	public void updateOutputStatistics(DiameterPacket packet,
			String hostIdentity) {
		
	}

	@Override
	public void updateTimeoutRequestStatistics(DiameterRequest request,
			String hostIdentity) {
		
	}

	@Override
	public void updateUnknownH2HDropStatistics(DiameterAnswer answer,
			String hostIdentity) {
		
	}

	@Override
	public void updateUnknownH2HDropStatistics(DiameterAnswer answer,
			String hostIdentity, String realmName, RoutingActions routeAction) {
		
	}

	@Override
	public void updateDiameterStatsMalformedPacket(DiameterPacket packet,
			String hostIdentity) {
		
	}

	@Override
	public void updateDiameterStatsPacketDroppedStatistics(
			DiameterPacket packet, String hostIdentity) {
		
	}

	@Override
	public void updateRealmTimeoutRequestStatistics(
			DiameterRequest diameterRequest, String realmName, RoutingActions routingAction) {
		
	}

	@Override
	public void updateDiameterStatsPacketDroppedStatistics(
			DiameterPacket packet, String hostIdentity, String realmName,
			RoutingActions routeAction) {
		
	}

	@Override
	public void updateRealmInputStatistics(DiameterPacket packet,
			String realmName, RoutingActions routeAction) {
		
	}

	@Override
	public void updateRealmOutputStatistics(DiameterPacket packet,
			String realmName, RoutingActions routeAction) {
		
	}

	@Override
	public void updateDuplicatePacketStatistics(DiameterPacket packet,
			String hostIdentity) {
		
	}

	@Override
	public void purgeCancelledTasks() {
	}

	@Override
	public ScheduledFuture<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
		return null;
	}

	@Override
	public boolean isEREnabled() {
		return false;
	}

	@Override
	public int getTotalActiveSessionCount() {
		return 0;
	}

	@Override
	public int getOverloadResultCode() {
		return 0;
	}

	@Override
	public OverloadAction getActionOnOverload() {
		return null;
	}

	@Override
	public boolean isOverLoad(DiameterRequest diameterRequest) {
		return false;
	}

	@Override
	public CDRDriver<DiameterPacket> getDiameterCDRDriver(String name)
			throws DriverInitializationFailedException,
			DriverNotFoundException, TypeNotSupportedException {
		return null;
	}

	@Override
	public DiameterStatisticsProvider getDiameterStatisticsProvider() {
		return null;
	}

	@Override
	public long releasePeerSessions(DiameterRequest basePacket) {
		return 0;
	}

	@Override
	public boolean isServerInitiatedMessage(int commandCode) {
		return false;
	}

	@Override
	public TaskScheduler getTaskScheduler() {
		return null;
	}

	@Override
	public void submitToWorker(PacketProcess packetProcess) {
		
	}

	@Override
	public int getMaxWorkerThreads() {
		return 0;
	}

	@Override
	public Session generateSession(long appId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VirtualConnectionHandler registerVirtualPeer(PeerData peerData, VirtualOutputStream outpurStream)
			throws ElementRegistrationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void scheduleSingleExecutionTask(Runnable command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getOrCreateSession(String sessionId, long appId) {
		return sessions.get(sessionId);
	}

	@Override
	public boolean hasSession(String sessionId, long appId) {
		return sessions.containsKey(sessionId);
	}

	@Override
	public ISession readOnlySession(String sessionId, long appId) {
		return sessions.get(sessionId);
	}

}
