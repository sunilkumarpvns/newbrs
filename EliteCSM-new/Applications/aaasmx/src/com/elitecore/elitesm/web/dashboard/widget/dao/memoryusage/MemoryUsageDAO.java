package com.elitecore.elitesm.web.dashboard.widget.dao.memoryusage;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.memoryusage.MemoryUsageData;

public interface MemoryUsageDAO {

	public List<MemoryUsageData> getMemoryUsageData(String serverKey) throws Exception;
	public List<MemoryUsageData> getMemoryUsageData(int interval) throws Exception;
	public List<MemoryUsageData> getMemoryUsageLiveData(String serverKey) throws Exception;
	
}
