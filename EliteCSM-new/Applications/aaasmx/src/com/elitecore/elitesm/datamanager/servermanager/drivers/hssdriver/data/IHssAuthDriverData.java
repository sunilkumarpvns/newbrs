package com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data;

import java.util.List;

import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData;


public interface IHssAuthDriverData {

	public String getHssauthdriverid();

	public void setHssauthdriverid(String hssauthdriverid);

	public String getApplicationid() ;

	public void setApplicationid(String applicationid);

	public Long getRequesttimeout();

	public void setRequesttimeout(Long requesttimeout);

	public String getDriverInstanceId();

	public void setDriverInstanceId(String driverInstanceId);

	public String getUserIdentityAttributes();

	public void setUserIdentityAttributes(String userIdentityAttributes) ;
	
	public List<HssAuthDriverFieldMapData> getHssAuthFieldMapList();

	public void setHssAuthFieldMapList( List<HssAuthDriverFieldMapData> hssAuthFieldMapList);

	public List<DiameterPeerRelData> getDiameterPeerRelDataList();

	public void setDiameterPeerRelDataList( List<DiameterPeerRelData> diameterPeerRelDataList);
}
