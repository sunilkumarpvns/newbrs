package com.elitecore.elitesm.web.dashboard.widget.dao.radacctesistatistics;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.radacctesistatistics.RadiusAcctESIStatData;

public interface RadiusAcctESIStatisticsDAO {
	
	public List<RadiusAcctESIStatData> getRadAuthESIDataList(String serverKey) throws Exception;
	public List<RadiusAcctESIStatData> getRadAuthESIStatisticsDetails(RadiusAcctESIStatData criteriaData)  throws Exception;
	public List<RadiusAcctESIStatData> getRadAuthESIDataList(int interval)  throws Exception;
	
}
