package com.elitecore.elitesm.web.dashboard.widget.dao.radauthesistatistics;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.radauthesistatistics.RadiusAuthESIStatData;

public interface RadiusAuthESIStatisticsDAO {
	
	public List<RadiusAuthESIStatData> getRadAuthESIDataList(String serverKey) throws Exception;
	public List<RadiusAuthESIStatData> getRadAuthESIStatisticsDetails(RadiusAuthESIStatData criteriaData)  throws Exception;
	public List<RadiusAuthESIStatData> getRadAuthESIDataList(int interval)throws Exception;
	public List<RadiusAuthESIStatData> getRadAuthESIStatisticsDetailsData(String serverKey)throws Exception;
}
