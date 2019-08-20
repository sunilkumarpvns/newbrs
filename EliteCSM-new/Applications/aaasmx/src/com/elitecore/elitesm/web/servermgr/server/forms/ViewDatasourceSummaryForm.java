package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewDatasourceSummaryForm extends BaseWebForm{
	private String status = "0";
	private String refreshTime = "0";
	private String instantRefresh="";
	static final long serialVersionUID=111111L;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(String  refreshTime) {
		this.refreshTime = refreshTime;
	}
	public void setInstantRefresh(String instantRefresh){
		this.instantRefresh=instantRefresh;
	}
	public String getInstantRefresh(){
		return instantRefresh;
	}
}
