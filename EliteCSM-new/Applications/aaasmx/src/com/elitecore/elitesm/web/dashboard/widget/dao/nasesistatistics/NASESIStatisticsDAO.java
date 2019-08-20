package com.elitecore.elitesm.web.dashboard.widget.dao.nasesistatistics;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.nasesistatistics.NasESIStatData;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctesistatistics.RadiusAcctESIStatData;

public interface NASESIStatisticsDAO {
	
	public List<NasESIStatData> getNasESIDataList(String serverKey) throws Exception;
	public List<NasESIStatData> getNasESIStatisticsDetails(NasESIStatData criteriaData)  throws Exception;
	public List<NasESIStatData> getNasESIDataList(int interval)  throws Exception;
	
	
}
