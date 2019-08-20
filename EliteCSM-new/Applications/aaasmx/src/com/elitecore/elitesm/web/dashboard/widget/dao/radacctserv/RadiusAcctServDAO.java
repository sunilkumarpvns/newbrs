package com.elitecore.elitesm.web.dashboard.widget.dao.radacctserv;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.radacctserv.RadiusAcctServData;

public interface RadiusAcctServDAO {
	public List<RadiusAcctServData> getAuthServDataList(String serverKey) throws Exception;
	public List<RadiusAcctServData> getAuthServDataList(int interval) throws Exception;
}
