package com.elitecore.netvertex.core.session;

import javax.annotation.Nonnull;

import com.elitecore.corenetvertex.util.CacheStatistics;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.SessionCacheStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class SessionCacheStatisticsProvider extends SessionCacheStatistics {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Nonnull transient private final CacheStatistics statistics;
	
	public SessionCacheStatisticsProvider(CacheStatistics statistics) {
		this.statistics = statistics;
	}

	@Override
	public Long getTotalAverageLoadPanelty() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(statistics.getAverageLoadPanelty());
	}

	@Override
	public Long getTotalLoadCount() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(statistics.getLoadCount());
	}

	@Override
	public Long getTotalRequestCount() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(statistics.getRequestCount());
	}

	@Override
	public Long getTotalMissedCount() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(statistics.getMissCount());
	}

	@Override
	public Long getTotalHitCount() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(statistics.getHitCount());
	}

	@Override
	public Long getTotalCacheCount() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(statistics.getCacheCount());
	}

	@Override
	public Long getTotalEvictionCount() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(statistics.getEvictionCount());
	}
}
