package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ViewReloadCacheDetailsForm extends BaseWebForm{
	private String name;
	private String netServerId;
	private String netServerType;
	private String description;
	private String version;
	private String action;
	private String errorCode;
	private List<CacheDetailBean> cacheDetails;
	
	
	public List<CacheDetailBean> getCacheDetails() {
		return cacheDetails;
	}
	public void setCacheDetails(List<CacheDetailBean> cacheDetails) {
		this.cacheDetails = cacheDetails;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}
	public String getNetServerType() {
		return netServerType;
	}
	public void setNetServerType(String netServerType) {
		this.netServerType = netServerType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
