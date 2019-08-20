package com.elitecore.diameterapi.mibs.statistics;

import java.util.Map;
import java.util.Set;

import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;

public interface DiameterStatisticsProvider {

	public Set<String> getApplicationsSet();

	public GroupedStatistics getStackStatistics();

	public Map<String, GroupedStatistics> getPeerStatsMap();

	public Map<RealmIdentifier, GroupedStatistics> getRealmStatsMap();

	public Map<ApplicationStatsIdentifier, GroupedStatistics> getApplicationMap();

	public Map<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> getApplicationPeerMap();

	public long getAvgIncomingMPS();

	public long getAvgRoundTripTime();
	
	public long getMessagePerMinute();
	
	public Set<ApplicationEnum> getSupportedApplicationIdentifiers();

	public Long geTotalOutMessages();

	public Long getTotalInMessages();

	long getLastResetTimeInMilli();

}
