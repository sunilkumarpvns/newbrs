package com.elitecore.diameterapi.mibs.base;

import java.util.Collection;
import java.util.Observer;
import java.util.Set;

import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeersTable;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterConfiguration;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatistic;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticResetter;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.MIBIndexRecorder;

public class DiameterStatisticListener {

	private DiameterConfiguration diameterConfiguration;
	private DiameterPeersTable diameterPeersTable;
	private DiameterStatistic diameterStatistic;

	public DiameterStatisticListener(MIBIndexRecorder mibIndexRecorder, DiameterPeersTable peersTable,
			Set<ApplicationEnum> supportedApplicationIdentifiers, TaskScheduler taskScheduler) {
		diameterPeersTable = peersTable;
		diameterConfiguration = new DiameterConfiguration(mibIndexRecorder);
		diameterStatistic = new DiameterStatistic(supportedApplicationIdentifiers,taskScheduler);
		
	}
	public void init(){
		diameterStatistic.init(diameterPeersTable.getPeerList());
		diameterConfiguration.init(diameterPeersTable.getPeerList());
	}
	
	public DiameterConfigProvider getDiameterConfigProvider(){
		return diameterConfiguration;
	}
	
	public  DiameterStatisticsProvider getDiameterStatisticProvider() {
		return diameterStatistic;
	}
	
	public void addDiameterPeer(DiameterPeer peer) {
		diameterConfiguration.addDiameterPeer(peer);
	}
	public void updateInputStatistics(DiameterPacket packet, String hostIdentity) {
		diameterStatistic.updateInputStatistics(packet, hostIdentity);
	}
	public void updateOutputStatistics(DiameterPacket packet, String hostIdentity) {
		diameterStatistic.updateOutputStatistics(packet, hostIdentity);
	}
	public void updateRealmInputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
		diameterStatistic.updateRealmInputStatistics(realmName, routeAction, packet);
	}
	public void updateRealmOutputStatistics(DiameterPacket packet, String realmName, RoutingActions routeAction) {
		diameterStatistic.updateRealmOutputStatistics(realmName, routeAction, packet);
	}
	public void updateTimeoutRequestStatistics(DiameterRequest request, String hostIdentity) {
		diameterStatistic.updateTimeoutRequestStatistics(request, hostIdentity);
	}
	public void updateRealmTimeoutRequestStatistics(DiameterRequest request, String realmName, RoutingActions routingAction) {
		diameterStatistic.updateRealmTimeoutRequestStatistics(request, realmName, routingAction);
	}
	public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity) {
		diameterStatistic.updateUnknownH2HDropStatistics(answer, hostIdentity);
	}
	public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity, String realmName, RoutingActions routeAction) {
		diameterStatistic.updateUnknownH2HDropStatistics(answer, hostIdentity, realmName, routeAction);
	}
	public void updateDuplicatePacketStatistics(DiameterPacket packet, String hostIdentity) {
		diameterStatistic.updateDuplicatePacketStatistics(packet, hostIdentity);
	}
	public void updateMalformedPacketCount(DiameterPacket packet, String hostIdentity) {
		diameterStatistic.updateMalformedPacketStatistics(packet, hostIdentity);
	}
	public void updatePacketDroppedStatistics(DiameterPacket packet, String hostIdentity) {
		diameterStatistic.updatePacketDroppedStatistics(packet, hostIdentity);
	}
	public void updatePacketDroppedStatistics(DiameterPacket packet, String hostIdentity, 
			String realmName, RoutingActions routeAction) {
		diameterStatistic.updatePacketDroppedStatistics(packet, hostIdentity, realmName, routeAction);
	}
//	public String getPeerState( String hostIdentity) {
//		return DiameterPeerState.fromStateOrdinal(diameterPeersTable.getPeerState(hostIdentity)).name();
//	}
	
	public  void addStatisticObserver(Observer statisticObserver) {
		diameterStatistic.addObserver(statisticObserver);
	}
	
	public  void addConfigurationObserver(Observer configurationObserver) {
		diameterConfiguration.addObserver(configurationObserver);
	}
	
	public void reload(Collection<DiameterPeer> peerList) {
		diameterConfiguration.reload(peerList);
	}
	public DiameterStatisticResetter getDiameterStatisticResetter() {
		return diameterStatistic;
	}
	
}
