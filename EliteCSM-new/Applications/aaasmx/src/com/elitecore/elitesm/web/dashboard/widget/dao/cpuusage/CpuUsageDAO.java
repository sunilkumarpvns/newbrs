package com.elitecore.elitesm.web.dashboard.widget.dao.cpuusage;

import java.util.List;
import java.util.Map;
import com.elitecore.elitesm.web.dashboard.widget.model.cpuusage.CpuUsageData;
public interface CpuUsageDAO {
	public Map<String,List<CpuUsageData>> getCpuUsageData(String serverKey)throws Exception;
	public Map<String,List<CpuUsageData>> getCpuUsageData(int interval)throws Exception;
	public List<CpuUsageData> getCpuUsageLiveData(String serverKey) throws Exception;

}
