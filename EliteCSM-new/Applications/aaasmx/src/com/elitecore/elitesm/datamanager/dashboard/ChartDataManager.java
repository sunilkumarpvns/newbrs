package com.elitecore.elitesm.datamanager.dashboard;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.MemoryUsageData;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.TotalRequestStatistics;

public interface ChartDataManager {
	
	public List<MemoryUsageData> getMemoryUsage() throws DataManagerException;
	public List<MemoryUsageData> getLastMemoryUsage() throws DataManagerException;
	public List<TotalRequestStatistics> getLatestTotalRequestStatistics() throws DataManagerException;
	public List<TotalRequestStatistics> getTotalRequestStatistics(TotalRequestStatistics totalReqStatisticsData) throws DataManagerException;
}
