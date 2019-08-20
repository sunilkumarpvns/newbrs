package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.extended;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.EnumUsageStatisticsReset;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.UsageStatistics;
import com.elitecore.netvertex.usagemetering.UsageMonitoringStatisticsCounter;
import com.sun.management.snmp.SnmpStatusException;

public class UsageMonitoringStatisticsProvider extends	UsageStatistics {
	
	private static final String MODULE = "USAGE-STATS-PROVIDER";
	public static final int STATS_RESET = 1;

	private transient UsageMonitoringStatisticsCounter usageMonitoringStatistics;

	public UsageMonitoringStatisticsProvider(
			UsageMonitoringStatisticsCounter usageMonitoringStatistics) {
		this.usageMonitoringStatistics =usageMonitoringStatistics;
	}

	@Override
	public Long getTotalUsageReportedInLast24Hours() {
		return usageMonitoringStatistics.getTotalUsageForLast24Hours();
	}

	@Override
	public Long getTotalUsageReportedInYesterday() {
		return usageMonitoringStatistics.getTotalUsageReportedInYesterDay();
	}

	@Override
	public Long getTotalUsageReportedInCurrentDay() {
		return usageMonitoringStatistics.getCurrentDayUsage();
	}

	@Override
	public Long getTotalUsageReportedInLastHour() {
		return usageMonitoringStatistics.getUsageReportedInLastHour();
	}
	
	@Override
	public void setUsageStatisticsReset(EnumUsageStatisticsReset enumUsageStatisticsReset) throws SnmpStatusException {
		if(enumUsageStatisticsReset == null){
			getLogger().error(MODULE, "Unable to reset usage statistics. Reason: usage statistics reset value received is null");
			throw new IllegalArgumentException();
		}

		if(enumUsageStatisticsReset.intValue() == STATS_RESET){
			usageMonitoringStatistics.reset();
		}else{
			getLogger().error(MODULE, "Unable to reset usage statistics. Reason: Invalid statistics reset value received: " 
					+ enumUsageStatisticsReset.intValue());
			throw new IllegalArgumentException("Unable to reset usage statistics. Reason: Invalid statistics reset value received: " 
					+ enumUsageStatisticsReset.intValue());
		}
	}
	
	/*
	 * This method gives 100th of seconds passed till last reset
	 */
	@Override
	public Long getUsageStatisticsResetTime() {
		return  (System.currentTimeMillis() - usageMonitoringStatistics.getStatisicsResetTime()) / 10;
	}

	public long getLastResetTimeInMillis() {
		return usageMonitoringStatistics.getStatisicsResetTime();
	}
}