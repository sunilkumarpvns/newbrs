package com.elitecore.elitesm.web.servermgr.service.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.servermgr.ChartTypeBean;

public class ViewServiceGraphForm extends BaseWebForm{

	
	
	private static final long serialVersionUID = 1L;
	
	private List<ChartTypeBean> chartList;

	public List<ChartTypeBean> getChartList() {
		return chartList;
	}

	public void setChartList(List<ChartTypeBean> chartList) {
		this.chartList = chartList;
	}
	
	
	
}
