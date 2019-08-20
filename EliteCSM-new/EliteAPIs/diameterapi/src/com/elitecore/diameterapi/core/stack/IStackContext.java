package com.elitecore.diameterapi.core.stack;

import java.util.concurrent.ScheduledFuture;

import com.elitecore.core.commons.tls.EliteSSLContextFactory;
import com.elitecore.core.serverx.internal.tasks.CallableSingleExecutionAsyncTask;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.transport.INetworkConnector;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;

public interface IStackContext {
	
	
	public INetworkConnector getNetworkConnector(TransportProtocols transportProtocol);
	
    public ScheduledFuture<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task);
    
    public <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(CallableSingleExecutionAsyncTask<T> task);
    
    public ScheduledFuture<?> scheduleIntervalBasedTask(IntervalBasedTask task);
    
    public void scheduleSingleExecutionTask(Runnable command);
    
    public void purgeCancelledTasks();
    
    public void finalPreResponseProcess(DiameterPacket packet) ;
    
    public PeerData getPeerData(String hostIdentity) ;

	public long getNextPeerSequence(String hostIdentity);

	public long getNextServerSequence();

	public String getNextSessionID();

	public String getNextSessionID(String optionalVal);

	boolean hasSession(String sessionId, long appId);
	ISession readOnlySession(String sessionId, long appId);
	public Session getOrCreateSession(String sessionId, long appId);
	public Session generateSession(long appId);
	public Session generateSession(String sessionIdSuffix, long appId);
	
	public EliteSSLContextFactory getEliteSSLContextFactory();
	
	public void updateInputStatistics(DiameterPacket packet, String hostIdentity);
	public void updateOutputStatistics(DiameterPacket packet, String hostIdentity);
	public void updateTimeoutRequestStatistics(DiameterRequest request, String hostIdentity);
	public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity);
	public void updateUnknownH2HDropStatistics(DiameterAnswer answer,String hostIdentity, String realmName, RoutingActions routeAction);
	public void updateDiameterStatsMalformedPacket(DiameterPacket packet, String hostIdentity);
	public void updateDiameterStatsPacketDroppedStatistics(DiameterPacket packet, String hostIdentity);
	public void updateRealmTimeoutRequestStatistics(DiameterRequest diameterRequest,String realmName, RoutingActions routingAction);
	public void updateDiameterStatsPacketDroppedStatistics(DiameterPacket packet,String hostIdentity, String realmName, RoutingActions routeAction);
	public void updateRealmInputStatistics(DiameterPacket packet, String realmName,RoutingActions routeAction);
	public void updateRealmOutputStatistics(DiameterPacket packet, String realmName,RoutingActions routeAction);
	public void updateDuplicatePacketStatistics(DiameterPacket packet, String hostIdentity);
	public int getOverloadResultCode();
	public OverloadAction getActionOnOverload();

	public TaskScheduler getTaskScheduler();
	
	/*
	 * Stuff added by me
	 */
	public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity); 
}
