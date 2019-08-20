package com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthserver;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthserver.RadiusDynaAuthServerData;

public interface RadiusDynaAuthServerDAO {
	public List<RadiusDynaAuthServerData> getRadiusDynaAuthServerDataList(String serverIp) throws Exception;
	public List<RadiusDynaAuthServerData> getRadiusDynaAuthServerDataList(int interval)  throws Exception;
}
