package com.elitecore.elitesm.web.dashboard.json;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class TemplateGlobalConfData extends BaseData{
	private String templateConfId;
	private Long refreshInterval;
	private String active;
	private String templateId;
	
	public String getTemplateConfId() {
		return templateConfId;
	}
	public void setTemplateConfId(String templateConfId) {
		this.templateConfId = templateConfId;
	}
	public Long getRefreshInterval() {
		return refreshInterval;
	}
	public void setRefreshInterval(Long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
}
