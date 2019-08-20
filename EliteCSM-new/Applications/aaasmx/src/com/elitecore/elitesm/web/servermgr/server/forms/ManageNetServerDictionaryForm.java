package com.elitecore.elitesm.web.servermgr.server.forms;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ManageNetServerDictionaryForm extends BaseWebForm{
	
	private String netServerId;
	private String action;
	private Map<String, List<LiveDictionaryData>> mapDictionary;
	private String errorCode;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public Map<String, List<LiveDictionaryData>> getMapDictionary() {
		return mapDictionary;
	}
	public void setMapDictionary(Map<String, List<LiveDictionaryData>> mapDictionary) {
		this.mapDictionary = mapDictionary;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}

}
