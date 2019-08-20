package com.elitecore.elitesm.web.dashboard.widget.dao.radauthclient;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.radauthclient.RadiusAuthClientData;

public interface RadiusAuthClientDAO {
	public List<RadiusAuthClientData> getRadiusAuthClientDataList(String serverKey) throws Exception;
	public List<RadiusAuthClientData> getRadiusAuthClientDataList(int interval) throws Exception;
}
