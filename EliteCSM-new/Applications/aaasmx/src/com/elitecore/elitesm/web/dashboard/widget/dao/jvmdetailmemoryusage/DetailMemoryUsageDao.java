package com.elitecore.elitesm.web.dashboard.widget.dao.jvmdetailmemoryusage;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory.JVMDetailMemoryTotalData;
import com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory.JVMDetailMemoryUsageData;

public interface DetailMemoryUsageDao {
	public List<JVMDetailMemoryTotalData> getDetailMemoryUsageData() throws Exception;
	public List<JVMDetailMemoryTotalData> getDetailMemoryUsageData(int interval) throws Exception;
	public List<JVMDetailMemoryUsageData> getDetailMemoryUsageDataList(String serverKey) throws Exception;
}
