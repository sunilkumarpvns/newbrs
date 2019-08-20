package com.elitecore.elitesm.web.dashboard.widget.dao.esi;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.model.esi.ESIWidgetData;

public interface ESIWidgetDAO {
	public List<ESIWidgetData> getESIWidgetDataList();
	public List<ESIWidgetData> getESIWidgetDataList(int interval);
}
