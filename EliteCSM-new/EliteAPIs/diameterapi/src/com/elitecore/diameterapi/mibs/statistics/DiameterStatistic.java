package com.elitecore.diameterapi.mibs.statistics;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;
import com.elitecore.diameterapi.mibs.custom.utility.DiameterStatisticsEvents;

public class DiameterStatistic extends Observable implements DiameterStatisticResetter , DiameterStatisticsProvider {
	private static final String MODULE = "DIAM-STATS";
	private Map<String, GroupedStatistics> peerStatisticsMap;	 
	private Map<RealmIdentifier, GroupedStatistics> realmStatisticsMap;
	private Map<ApplicationStatsIdentifier, GroupedStatistics> applicationStatisticsMap;
	private Map<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> appSpeceficPeerStatisticsMap;
	private Set<ApplicationEnum> supportedApplicationEnums;
	private GroupedStatistics stackStatistics;
	
	//Please do not reset this counters	
	private AtomicLong requestCounter;	
	private AtomicLong responseCounter;
	private AtomicLong totalResponseTimeInMillis;
	
	private long avgIncomingMPS;
	private long avgRoundTripTimeMS;
	private long lastResetTimeInMilli;
	
	public DiameterStatistic(Set<ApplicationEnum> supportedApplicationEnums, TaskScheduler taskScheduler){

		this.supportedApplicationEnums = supportedApplicationEnums;
		stackStatistics = new GroupedStatistics();
		peerStatisticsMap = new ConcurrentHashMap<String, GroupedStatistics>();
		realmStatisticsMap = new ConcurrentHashMap<RealmIdentifier, GroupedStatistics>();
		applicationStatisticsMap = new ConcurrentHashMap<ApplicationStatsIdentifier, GroupedStatistics>();
		appSpeceficPeerStatisticsMap = new ConcurrentHashMap<ApplicationStatsIdentifier, Map<String,GroupedStatistics>>();
		requestCounter = new AtomicLong();
		responseCounter = new AtomicLong();
		totalResponseTimeInMillis = new AtomicLong();
		taskScheduler.scheduleIntervalBasedTask(new MPSCalculator());		
	}
 

	public void init(Collection<DiameterPeer> peerList) {
		initApplicationMap();
		initPeerStatistics(peerList);
	}

	private void initPeerStatistics(Collection<DiameterPeer> peerList) {
		if(peerList != null){
			for(DiameterPeer peer : peerList){
				if(peer != null && Strings.isNullOrBlank(peer.getHostIdentity()) == false)
					peerStatisticsMap.put(peer.getHostIdentity(), new GroupedStatistics());
			}
		}
	}

	private void initApplicationMap() {
		
		applicationStatisticsMap.put(getApplicationStatsIdentifier(ApplicationIdentifier.BASE), new GroupedStatistics());
		if(supportedApplicationEnums != null){
			for(ApplicationEnum appEnum : supportedApplicationEnums){
				applicationStatisticsMap.put(getApplicationStatsIdentifier(appEnum), new GroupedStatistics());
			}
		}
	}

	/**
	 * 
	 * @param applicationEnum 
	 * @return ApplicationStatsIdentifier
	 * 
	 */
	private ApplicationStatsIdentifier getApplicationStatsIdentifier(ApplicationEnum applicationEnum) {
		return new ApplicationStatsIdentifier(applicationEnum.getApplicationId(), 
				applicationEnum.getVendorId(), applicationEnum.getApplication().getDisplayName());
	}

	public void updateInputStatistics(DiameterPacket packet, String hostIdentity) {
		if(packet.getApplicationID() != ApplicationIdentifier.BASE.getApplicationId()){			
			requestCounter.incrementAndGet();
		}
		updatePeerInputStatistics(packet, hostIdentity);
		updateApplicationInputStatistics(packet, hostIdentity);
		stackStatistics.incrementInputStatistics(packet);
	}

	public void updateOutputStatistics(DiameterPacket packet, String hostIdentity) {
		
		if(packet.isResponse() && packet.getApplicationID() != ApplicationIdentifier.BASE.getApplicationId()){
			if(((DiameterAnswer)packet).getRequestReceivedTime()>0){
				totalResponseTimeInMillis.addAndGet(System.currentTimeMillis() - ((DiameterAnswer)packet).getRequestReceivedTime());
				responseCounter.incrementAndGet();
			}
		}
		
		updatePeerOutputStatistics(packet, hostIdentity);
		updateApplicationOutputStatistics(packet, hostIdentity);
		stackStatistics.incrementOutputStatistics(packet);
	}

	private void updatePeerInputStatistics(DiameterPacket packet, String hostIdentity) {

		GroupedStatistics peerStatistics = getPeerStatistic(hostIdentity);
		if(peerStatistics == null){
			return;
		}
		peerStatistics.incrementInputStatistics(packet);
	}

	private void updatePeerOutputStatistics(DiameterPacket packet, String hostIdentity) {

		GroupedStatistics peerStatistics = getPeerStatistic(hostIdentity);
		if(peerStatistics == null){
			return;
		}
		peerStatistics.incrementOutputStatistics(packet);
	}

	@Nullable
	private GroupedStatistics getPeerStatistic(String peerIdentity) {

		if (Strings.isNullOrBlank(peerIdentity)) {
			return null;
		}
		
		GroupedStatistics peerStatistics = peerStatisticsMap.get(peerIdentity);
		if(peerStatistics == null){
			synchronized (peerStatisticsMap) {
				peerStatistics = peerStatisticsMap.get(peerIdentity);
				if(peerStatistics == null){
					peerStatistics = new GroupedStatistics();
					peerStatisticsMap.put(peerIdentity, peerStatistics);
					setChanged();
					notifyObservers(peerIdentity);
				}
			}
		}
		return peerStatistics;
	}

	public void updateRealmInputStatistics(String realmName, 
			RoutingActions routeAction, DiameterPacket packet) {

		GroupedStatistics realmStatistics = getRealmStatistic(realmName, routeAction, packet);
		if(realmStatistics == null){
			return;
		}
		realmStatistics.incrementInputStatistics(packet);
	}
	
	public void updateRealmOutputStatistics(String realmName, 
			RoutingActions routeAction, DiameterPacket packet) {

		GroupedStatistics realmStatistics = getRealmStatistic(realmName, routeAction, packet);
		if(realmStatistics == null){
			return;
		}
		realmStatistics.incrementOutputStatistics(packet);
	}

	private GroupedStatistics getRealmStatistic(String realmName, RoutingActions routeAction, DiameterPacket packet) {
		RealmIdentifier realmIdentifier = getRealmIdentifier(packet, realmName, routeAction);
		if(realmIdentifier == null){
			return null;
		}
		GroupedStatistics realmStatistics = realmStatisticsMap.get(realmIdentifier);
		if(realmStatistics == null){
			synchronized (realmStatisticsMap) {
				realmStatistics = realmStatisticsMap.get(realmIdentifier);
				if(realmStatistics == null){
					realmStatistics = new GroupedStatistics();
					realmStatisticsMap.put(realmIdentifier, realmStatistics);
					setChanged();
					notifyObservers(realmIdentifier);
				}
			}
		}
		return realmStatistics;
	}

	private RealmIdentifier getRealmIdentifier(DiameterPacket packet, String realmName, RoutingActions routeAction) {
		ApplicationEnum applicationEnum = getApplicationEnum(packet);
		if(applicationEnum == null || applicationEnum.getApplication() == null || realmName == null || routeAction == null)
			return null;
		return new RealmIdentifier(realmName, applicationEnum.getApplicationId(), applicationEnum.getApplicationType(), routeAction);
	}

	private void updateApplicationInputStatistics(DiameterPacket packet, String hostIdentity) { 
		ApplicationEnum appEnum = getApplicationEnum(packet);
		if(appEnum == null){
			return;
		}
		ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
		GroupedStatistics applicationStats = getApplicationStatistic(packet, applicationStr);
		if(applicationStats == null){
			return;
		}	
		applicationStats.incrementInputStatistics(packet);
		
		GroupedStatistics peerStatistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
		if(peerStatistics == null){
			return;
		}
		peerStatistics.incrementInputStatistics(packet);
	}
	
	private void updateApplicationOutputStatistics(DiameterPacket packet, String hostIdentity) {
		ApplicationEnum appEnum = getApplicationEnum(packet);
		if(appEnum == null){
			return;
		}
		ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
		GroupedStatistics applicationStats = getApplicationStatistic(packet, applicationStr);
		if(applicationStats == null){
			return;
		}
		applicationStats.incrementOutputStatistics(packet);	
		
		GroupedStatistics peerStatistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
		if(peerStatistics == null){
			return;
		}
		peerStatistics.incrementOutputStatistics(packet);		
	}
	
	private GroupedStatistics getApplicationStatistic(DiameterPacket packet, ApplicationStatsIdentifier applicationStatsId) {
		GroupedStatistics applicationStats = applicationStatisticsMap.get(applicationStatsId);
		if(applicationStats == null){
			synchronized (applicationStatisticsMap) {
				applicationStats = applicationStatisticsMap.get(applicationStatsId);
				if(applicationStats == null){
					applicationStats = new GroupedStatistics();
					applicationStatisticsMap.put(applicationStatsId, applicationStats);
					setChanged();
					notifyObservers(applicationStatsId);
				}
			}
		}
		return applicationStats;
	}

	@Nullable
	private GroupedStatistics getApplicationPeerStatistic(ApplicationStatsIdentifier applicationStatsId, 
			String hostIdentity, DiameterPacket packet) {
		
		if (Strings.isNullOrBlank(hostIdentity)) {
			return null;
		}
		
		/*
		 * Need to notify the observers that a new Application-wise Peer entry has been added
		 */
		boolean needToNotify = false;
		
		Map<String, GroupedStatistics> appPeerStatsMap = appSpeceficPeerStatisticsMap.get(applicationStatsId);
		if(appPeerStatsMap == null){
			synchronized (appSpeceficPeerStatisticsMap) {
				appPeerStatsMap = appSpeceficPeerStatisticsMap.get(applicationStatsId);
				if(appPeerStatsMap == null){
					appPeerStatsMap = new ConcurrentHashMap<String, GroupedStatistics>();
					appSpeceficPeerStatisticsMap.put(applicationStatsId, appPeerStatsMap);
					needToNotify = true;
				}
			}
		}
		GroupedStatistics peerStatistics = appPeerStatsMap.get(hostIdentity);
		if(peerStatistics == null){
			synchronized (appSpeceficPeerStatisticsMap) {
				peerStatistics = appPeerStatsMap.get(hostIdentity);
				if(peerStatistics == null){
					peerStatistics = new GroupedStatistics();
					appPeerStatsMap.put(hostIdentity, peerStatistics);
					needToNotify = true;
				}
			}
		}
		
		if (needToNotify) {
			setChanged();
			notifyObservers(new DiameterStatisticsEvents(applicationStatsId, hostIdentity));
		}
		/*
		 * Need to notify the observers that a new Application-wise Peer-wise Command-Code entry has been added
		 */
		needToNotify = peerStatistics.getCommandCodeCountersMap().containsKey(packet.getCommandCode()) == false;
		if (needToNotify) {
			synchronized (peerStatistics) {

				needToNotify = peerStatistics.getCommandCodeCountersMap().containsKey(packet.getCommandCode()) == false;
				if (needToNotify) {
					
					/*
					 * Below line will create Command-Code entry
					 */
					peerStatistics.getCommandCodeCounterTuple(packet);
					
					setChanged();
					notifyObservers(new DiameterStatisticsEvents(applicationStatsId, hostIdentity, packet.getCommandCode()));
				}
			}
		}
		/*
		 * Need to notify the observers that a new Application-wise Peer-wise Result-Code entry has been added
		 */
		if (packet.isResponse()) {
			
			IDiameterAVP resultCodeAvp = packet.getAVP(DiameterAVPConstants.RESULT_CODE);
			if (resultCodeAvp == null) {
				resultCodeAvp = packet.getAVP(DiameterAVPConstants.EXPERIMENTAL_RESULT + 
						"." + DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE);
			}
			if (resultCodeAvp != null) {
				needToNotify = peerStatistics.getResultCodeCountersMap().containsKey((int)resultCodeAvp.getInteger()) == false;
				if (needToNotify) {
					
					synchronized (peerStatistics) {
						
						needToNotify = peerStatistics.getResultCodeCountersMap().containsKey((int)resultCodeAvp.getInteger()) == false;
						if (needToNotify) {
							
							/*
							 * Below line will create ResultCode Entry
							 */
							peerStatistics.getResultCodeTuple(packet.getCommandCode(), (int) resultCodeAvp.getInteger());
							
							setChanged();
							notifyObservers(new DiameterStatisticsEvents(applicationStatsId, hostIdentity, resultCodeAvp.getInteger()));
						}
					}					
				}
			}
		}
		return peerStatistics;
	}

	/**
	 *       --> Get AppId
	 *       --> If Base App Id return BASE AppIdentifier 
	 * <br />--> Get Application Enum from Supported Application Identifiers List
	 * <br />--> If not available then, get Application Enum from {@link ApplicationIdentifier}
	 * <br />--> If still not Available then, Create Application Enum
	 * 
	 * @param packet
	 * @return ApplicationEnum
	 */
	private ApplicationEnum getApplicationEnum(DiameterPacket packet) {

		final long applicationId = packet.getApplicationID();
		if(applicationId == 0){
			return ApplicationIdentifier.BASE;
		}

		for(ApplicationEnum applicationEnum : supportedApplicationEnums){
			if(applicationId == applicationEnum.getApplicationId()){ 
				return applicationEnum;
			}
		}
		ApplicationEnum appEnum = ApplicationIdentifier.fromApplicationIdentifiers(applicationId);

		if(appEnum != null){
			return appEnum;
		}
		long vendorId = ApplicationIdentifier.BASE.getVendorId();
		
		AvpGrouped vendorSpeceficAppId = (AvpGrouped) packet.getAVP(DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID);
		if(vendorSpeceficAppId != null){
			IDiameterAVP vendorIdAvp = vendorSpeceficAppId.getSubAttribute(DiameterAVPConstants.VENDOR_ID_INT);
			if(vendorIdAvp != null ){
			vendorId = vendorIdAvp.getInteger();
		}
			}
		final long finalVendorId = vendorId;
		
		return new ApplicationEnum() {

				@Override
				public long getVendorId() {
				return finalVendorId;
				}

				@Override
				public ServiceTypes getApplicationType() {
					return ServiceTypes.BOTH;
				}

				@Override
				public long getApplicationId() {
				return applicationId;
				}

				@Override
				public Application getApplication() {
					return Application.UNKNOWN;
				}
				
				@Override
				public String toString() {
					return new StringBuilder()
					.append(getVendorId())
					.append(":")
					.append(getApplicationId())
					.append(" [").append(getApplication().getDisplayName()).append("]").toString();
				}
			};
		}

	public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity) {
		
		stackStatistics.incrementUnknownH2HDropCount(answer);
		GroupedStatistics statistics = getPeerStatistic(hostIdentity);
		if(statistics != null){
			statistics.incrementUnknownH2HDropCount(answer);
		}
		ApplicationEnum appEnum = getApplicationEnum(answer);
		if(appEnum == null){
			return;
		}
		ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
		statistics = getApplicationStatistic(answer, applicationStr);
		if(statistics != null){
			statistics.incrementUnknownH2HDropCount(answer);
		}
		statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, answer);
		if(statistics != null){
			statistics.incrementUnknownH2HDropCount(answer);
		}
	}
	
	public void updateUnknownH2HDropStatistics(DiameterAnswer answer, String hostIdentity, String realmName, RoutingActions routeAction) {
		
		stackStatistics.incrementUnknownH2HDropCount(answer);
		GroupedStatistics statistics = getPeerStatistic(hostIdentity);
		if(statistics != null){
			statistics.incrementUnknownH2HDropCount(answer);
		}
		statistics = getRealmStatistic(realmName, routeAction, answer);
		if(statistics != null){
			statistics.incrementUnknownH2HDropCount(answer);
		}
		ApplicationEnum appEnum = getApplicationEnum(answer);
		if(appEnum == null){
			return;
		}
		ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
		statistics = getApplicationStatistic(answer, applicationStr);
		if(statistics != null){
			statistics.incrementUnknownH2HDropCount(answer);
		}
		statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, answer);
		if(statistics != null){
			statistics.incrementUnknownH2HDropCount(answer);
		}
	}

	public void updateMalformedPacketStatistics(DiameterPacket packet, String hostIdentity) {
		
		stackStatistics.incrementMalformedPacketCount(packet);
		GroupedStatistics statistics = getPeerStatistic(hostIdentity);
		if(statistics != null){
			statistics.incrementMalformedPacketCount(packet);
		}
		ApplicationEnum appEnum = getApplicationEnum(packet);
		if(appEnum == null){
			return;
		}
		ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
		statistics = getApplicationStatistic(packet, applicationStr);
		if(statistics != null){
			statistics.incrementMalformedPacketCount(packet);
		}
		statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
		if(statistics != null){
			statistics.incrementMalformedPacketCount(packet);
		}
	}
	
	public void updateDuplicatePacketStatistics(DiameterPacket packet, String hostIdentity) {
		
		stackStatistics.incrementDuplicatePacketCount(packet);
		GroupedStatistics statistics = getPeerStatistic(hostIdentity);
		if(statistics != null){
			statistics.incrementDuplicatePacketCount(packet);
		}
		ApplicationEnum appEnum = getApplicationEnum(packet);
		if(appEnum == null){
			return;
		}
		ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
		statistics = getApplicationStatistic(packet, applicationStr);
		if(statistics != null){
			statistics.incrementDuplicatePacketCount(packet);
		}
		statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
		if(statistics != null){
			statistics.incrementDuplicatePacketCount(packet);
		}
	}
	
	public void updateTimeoutRequestStatistics(DiameterRequest request, String hostIdentity){
		
		stackStatistics.incrementTimeoutRequestCount(request);
		GroupedStatistics statistics = getPeerStatistic(hostIdentity);
		if(statistics != null){
			statistics.incrementTimeoutRequestCount(request);
		}
		ApplicationEnum appEnum = getApplicationEnum(request);
		if(appEnum == null){
			return;
		}
		ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
		statistics = getApplicationStatistic(request, applicationStr);
		if(statistics != null){
			statistics.incrementTimeoutRequestCount(request);
		}
		statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, request);
		if(statistics != null){
			statistics.incrementTimeoutRequestCount(request);
		}
	}
	
	public void updateRealmTimeoutRequestStatistics(DiameterRequest request,
			String realmName, RoutingActions routingAction) {
		GroupedStatistics statistics = getRealmStatistic(realmName, routingAction, request);
		if(statistics != null){
			statistics.incrementTimeoutRequestCount(request);
		}
	}
	
	public void updatePacketDroppedStatistics(DiameterPacket packet, String hostIdentity){
		
		stackStatistics.incrementPacketDroppedCount(packet);
		GroupedStatistics statistics = getPeerStatistic(hostIdentity);
		if(statistics != null){
			statistics.incrementPacketDroppedCount(packet);
		}
		ApplicationEnum appEnum = getApplicationEnum(packet);
		if(appEnum == null){
			return;
		}
		ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
		statistics = getApplicationStatistic(packet, applicationStr);
		if(statistics != null){
			statistics.incrementPacketDroppedCount(packet);
		}
		statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
		if(statistics != null){
			statistics.incrementPacketDroppedCount(packet);
		}
	}
	
	public void updatePacketDroppedStatistics(DiameterPacket packet, 
			String hostIdentity, String realmName, RoutingActions routeAction){
		
		stackStatistics.incrementPacketDroppedCount(packet);
		GroupedStatistics statistics = getPeerStatistic(hostIdentity);
		if(statistics != null){
			statistics.incrementPacketDroppedCount(packet);
		}
		statistics = getRealmStatistic(realmName, routeAction, packet);
		if(statistics != null){
			statistics.incrementPacketDroppedCount(packet);
		}
		ApplicationEnum appEnum = getApplicationEnum(packet);
		if(appEnum == null){
			return;
		}
		ApplicationStatsIdentifier applicationStr = getApplicationStatsIdentifier(appEnum);
		statistics = getApplicationStatistic(packet, applicationStr);
		if(statistics != null){
			statistics.incrementPacketDroppedCount(packet);
		}
		statistics = getApplicationPeerStatistic(applicationStr, hostIdentity, packet);
		if(statistics != null){
			statistics.incrementPacketDroppedCount(packet);
		}
	}
	
	@Override
	public Set<String> getApplicationsSet() {
		
		Set<String> appKeys = new TreeSet<String>();
		for(ApplicationStatsIdentifier appStatsIdentifier : applicationStatisticsMap.keySet()){
			appKeys.add(appStatsIdentifier.getApplication());
		}
		return appKeys;
	}
	
	@Override
	public boolean reset() {
		resetStackStatistics();
		resetAllPeerStatistics();
		resetAllRealmStatistics();
		resetAllApplicationStatistics();
		return true;
	}
	
	@Override
	public boolean resetStackStatistics() {
		stackStatistics = new GroupedStatistics();
		return true;
	}
	
	@Override
	public boolean resetAllPeerStatistics(){
		for(String peerHostId : peerStatisticsMap.keySet()){
			peerStatisticsMap.put(peerHostId, new GroupedStatistics());
		}
		return true;
	}
	
	@Override
	public boolean resetAllRealmStatistics(){
		for(RealmIdentifier realmIdentifier : realmStatisticsMap.keySet()){
			realmStatisticsMap.put(realmIdentifier, new GroupedStatistics());
		}
		return true;
	}
	
	@Override
	public boolean resetApplicationStatistics(String applicationStr) {
		boolean found = false;
		for(ApplicationStatsIdentifier appStatsIdentifier : applicationStatisticsMap.keySet()){
			if(appStatsIdentifier.getApplication().equalsIgnoreCase(applicationStr) 
					|| String.valueOf(appStatsIdentifier.getApplicationId()).equals(applicationStr)){
				applicationStatisticsMap.put(appStatsIdentifier, new GroupedStatistics());
				found = true;
			}
		}
		return found;
	}

	@Override
	public boolean resetApplicationAllPeerStatistics(String applicationStr) {
		boolean found = false;
		for(Entry<ApplicationStatsIdentifier, Map<String, GroupedStatistics>>  entry: appSpeceficPeerStatisticsMap.entrySet()){
			if(entry.getKey().getApplication().equalsIgnoreCase(applicationStr) 
					|| String.valueOf(entry.getKey().getApplicationId()).equals(applicationStr)){
				for(String peerHostId : entry.getValue().keySet()){
					entry.getValue().put(peerHostId, new GroupedStatistics());
				}
				found = true;
			}
		}
		return found;
	}

	@Override
	public boolean resetApplicationPeerStatistics(String applicationStr, String hostIdentity) {
		boolean found = false;
		for(Map.Entry<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> appEntry : appSpeceficPeerStatisticsMap.entrySet()){
			if(appEntry.getKey().getApplication().equalsIgnoreCase(applicationStr) 
					|| String.valueOf(appEntry.getKey().getApplicationId()).equals(applicationStr)){
				if(appEntry.getValue().containsKey(hostIdentity)){
					appEntry.getValue().put(hostIdentity, new GroupedStatistics());
					found = true;
				}
			}
		}
		return found;
	}

	@Override
	public boolean resetPeerStatistics(String hostIdentity) {
		boolean found = false;
		if(peerStatisticsMap.containsKey(hostIdentity)){
			peerStatisticsMap.put(hostIdentity, new GroupedStatistics());
			found = true;
		}
		return found;
	}

	@Override
	public boolean resetRealmStatistics(String realmName) {
		boolean found = false;
		for(RealmIdentifier realmIdentifier : realmStatisticsMap.keySet()){
			if(realmIdentifier.getDbpRealmMessageRouteRealm().equals(realmName)){
				realmStatisticsMap.put(realmIdentifier, new GroupedStatistics());
				found = true;
			}
		}
		return found;
	}
	
	@Override
	public boolean resetAllApplicationStatistics(){
		initApplicationMap();
		for(Map<String, GroupedStatistics> appEntry : appSpeceficPeerStatisticsMap.values()){
			for (String peerHostId: appEntry.keySet()) {
				appEntry.put(peerHostId, new GroupedStatistics());
			}
		}
		return true;
	}
	
	@Override
	public GroupedStatistics getStackStatistics() {
		return stackStatistics;
	}

	@Override
	public Map<String, GroupedStatistics> getPeerStatsMap() {
		return peerStatisticsMap;
	}

	@Override
	public Map<RealmIdentifier, GroupedStatistics> getRealmStatsMap() {
		return realmStatisticsMap;
	}

	@Override
	public Map<ApplicationStatsIdentifier, GroupedStatistics> getApplicationMap() {
		return applicationStatisticsMap;
	}

	@Override
	public Map<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> getApplicationPeerMap() {
		return appSpeceficPeerStatisticsMap;
	}
	private class MPSCalculator extends BaseIntervalBasedTask{

		public MPSCalculator(){
			lastResetTimeInMilli = System.currentTimeMillis();
		}
		
		@Override
		public long getInitialDelay() {
			return 60;
		}

		@Override
		public long getInterval() {
			return 60;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			
			long totalRequestCount  = requestCounter.get();
			long totalResponseCount = responseCounter.get();			
			long tempTotalResponseTime = totalResponseTimeInMillis.get(); 
			
			requestCounter.set(0);
			responseCounter.set(0);			
			totalResponseTimeInMillis.set(0);
			
			long currentTimeInMilli = System.currentTimeMillis();
			long timeDiffInSec = (currentTimeInMilli - getLastResetTimeInMilli())/1000;
			lastResetTimeInMilli = currentTimeInMilli;
			
			avgIncomingMPS = (totalRequestCount / timeDiffInSec); 
			avgRoundTripTimeMS =  totalResponseCount > 0 ? tempTotalResponseTime / totalResponseCount : 0;
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Total Request Count = "+ totalRequestCount);
				LogManager.getLogger().debug(MODULE, "Total Response Count = "+ totalResponseCount);
				LogManager.getLogger().debug(MODULE, "Total Response Time = "+ tempTotalResponseTime + " ms");
			}			
			LogManager.getLogger().warn(MODULE, "Average incomming MPS = "+avgIncomingMPS+", Average Round Trip Time = "+avgRoundTripTimeMS+" ms for last 1 minute");			
		}
	}
	
	public long getAvgIncomingMPS() {
		return avgIncomingMPS;
	}
	public long getAvgRoundTripTime() {
		return avgRoundTripTimeMS;
	}
	
	public long getMessagePerMinute() {
		return requestCounter.get();
	}

	@Override
	public Set<ApplicationEnum> getSupportedApplicationIdentifiers() {
		return supportedApplicationEnums;
	}

	@Override
	public Long geTotalOutMessages() {
		return stackStatistics.getTotalRequestOutCount() + stackStatistics.getTotalAnswerOutCount();
	}

	@Override
	public Long getTotalInMessages() {
		return stackStatistics.getTotalRequestInCount() + stackStatistics.getTotalAnswerInCount();
	}

	@Override
	public long getLastResetTimeInMilli() {
		return lastResetTimeInMilli;
	}

}