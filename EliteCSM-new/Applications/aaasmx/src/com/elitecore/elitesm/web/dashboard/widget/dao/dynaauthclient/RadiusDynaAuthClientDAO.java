package com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthclient;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthclient.RadiusDynaAuthClientData;

public interface RadiusDynaAuthClientDAO {
	public List<RadiusDynaAuthClientData> getRadiusDynaAuthClientDataList(String clientIp) throws Exception;
	public List<RadiusDynaAuthClientData> getRadiusDynaAuthClientDataList(int interval)  throws Exception;
}
