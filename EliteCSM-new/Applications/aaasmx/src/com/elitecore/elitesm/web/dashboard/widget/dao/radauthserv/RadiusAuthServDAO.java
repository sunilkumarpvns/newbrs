package com.elitecore.elitesm.web.dashboard.widget.dao.radauthserv;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.radauthserv.RadiusAuthServData;

public interface RadiusAuthServDAO {
	public List<RadiusAuthServData> getAuthServDataList(String serverKey) throws Exception;
	public List<RadiusAuthServData> getAuthServDataList(int interval) throws Exception;
}
