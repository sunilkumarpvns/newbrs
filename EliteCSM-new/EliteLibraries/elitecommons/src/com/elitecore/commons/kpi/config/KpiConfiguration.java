package com.elitecore.commons.kpi.config;

public interface KpiConfiguration {
	
	static final int KPI_MAX_ROLLED_UNIT_IN_KB = (100 * 1024);		// 100 MB
	static final int KPI_ROLLING_UNIT_IN_KB = (50 * 1024);			// 50 MB
	static final String KPI_THREAD_KEY = "KPI";
	static final String KPI_THREAD_NAME_PREFIX = "KPI-SCH";

	public int getMaxNoOfThreads();

	public long getQueryInterval();

	public long getDumpInterval();

	public int getBatchSize();

	public String getDSName();
}
