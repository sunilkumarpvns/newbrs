package com.elitecore.diameterapi.diameter.stack;

import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import com.elitecore.commons.base.FixedTimeSource;
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
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicatorFactory;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException.ListenerRegFailResultCode;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionsFactory;
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
import com.elitecore.diameterapi.diameter.common.peers.DummyPeerProvider;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.session.InMemorySessionFactory;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.PacketProcess;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatistic;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;

public class DummyStackContext implements IDiameterStackContext {

	private int sessionId = 1;
	private Map<String , PeerData> peerDatas = new HashMap<String, PeerData>();
	private SessionsFactory sessionFactory = new InMemorySessionFactory(this, FixedTimeSource.systemTimeSource());
	
	private INetworkConnector networkConnector;
	private Set<ApplicationEnum> applicationIdentifiers = new HashSet<ApplicationEnum>();
	private DiameterPeerCommunicatorFactory commFactory;
	private DiameterStatistic diameterStatistic;
	private DummyPeerProvider peerProvider;
	
	public DummyStackContext(DummyPeerProvider peerProvider) {
		this.peerProvider = peerProvider;
		this.commFactory = new DiameterPeerCommunicatorFactory(this, peerProvider);
		this.diameterStatistic = new DiameterStatistic(getApplicationsIdentifiersList(), getTaskScheduler());
	}
	
	public void addSession(String sessionId) { sessionFactory.getOrCreateSession(sessionId); }

	@Override
	public INetworkConnector getNetworkConnector(TransportProtocols transportProtocol) { return networkConnector; }
	
	public void setNetworkConnector(INetworkConnector iNetworkConnector) { this.networkConnector = iNetworkConnector; }

	@Override
	public void finalPreResponseProcess(DiameterPacket packet) {}

	@Override
	public PeerData getPeerData(String hostIdentity) { return peerDatas.get(hostIdentity); }
	
	public void addPeerData(PeerData peerData) { 
		peerDatas.put(peerData.getHostIdentity(), peerData);
		peerDatas.put(peerData.getPeerName(), peerData);
	}

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
		diameterStatistic.updateInputStatistics(packet, hostIdentity);
	}

	@Override
	public void updateOutputStatistics(DiameterPacket packet,
			String hostIdentity) {
		diameterStatistic.updateOutputStatistics(packet, hostIdentity);
	}

	@Override
	public void updateTimeoutRequestStatistics(DiameterRequest request,
			String hostIdentity) {
		diameterStatistic.updateTimeoutRequestStatistics(request, hostIdentity);
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
		return diameterStatistic;
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
		return mock(TaskScheduler.class);
	}

	@Override
	public void submitToWorker(PacketProcess packetProcess) {
		
	}

	@Override
	public int getMaxWorkerThreads() {
		return 1;
	}

	@Override
	public VirtualConnectionHandler registerVirtualPeer(PeerData peerData, VirtualOutputStream outputStream)
			throws ElementRegistrationFailedException {
		return new VirtualConnectionHandler(mock(DiameterStack.class), peerData, outputStream);
	}

	@Override
	public Session generateSession(long appId) {
		return null;
	}

	@Override
	public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
		return commFactory.createInstance(hostIdentity);
	}

	public SessionsFactory getSessionFactory() {
		return sessionFactory;
	}

	public Collection<PeerData> getAllPeerData() {
		return peerDatas.values();
	}

	@Override
	public void scheduleSingleExecutionTask(Runnable command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Session getOrCreateSession(String sessionId, long appId) {
		return sessionFactory.getOrCreateSession(sessionId);
	}
	@Override
	public boolean hasSession(String sessionId, long appId) {
		return sessionFactory.hasSession(sessionId);
	}

	@Override
	public ISession readOnlySession(String sessionId, long appId) {
		return sessionFactory.readOnlySession(sessionId);
	}
	
	public DiameterPeerSpy registerPeerSpy(PeerData peerData) {
		addPeerData(peerData);
		DiameterPeerSpy peerSpy = 
				new DiameterPeerSpy(this, peerData);
		peerProvider.addPeer(peerSpy.getDiameterPeer());
		return peerSpy;
	}
}
