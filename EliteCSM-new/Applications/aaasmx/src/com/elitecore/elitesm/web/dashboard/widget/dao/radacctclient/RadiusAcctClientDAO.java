package com.elitecore.elitesm.web.dashboard.widget.dao.radacctclient;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.radacctclient.RadiusAcctClientData;

public interface RadiusAcctClientDAO {
	public List<RadiusAcctClientData> getRadiusAcctClientDataList(String clientAddress) throws Exception;
	public List<RadiusAcctClientData> getRadiusAcctClientDataList(int interval)  throws Exception;
}
