package com.elitecore.elitesm.datamanager.servermgr.script.data;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class ScriptData extends BaseData implements Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;
	private String scriptDataId;
	
	private String scriptId;
	
	private byte[] scriptFile ;
	private String scriptFileName;
	private Timestamp lastUpdatedTime;
	private Long date;
	private String scriptFileText;
	private Integer orderNumber;
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getScriptDataId() {
		return scriptDataId;
	}

	public void setScriptDataId(String scriptDataId) {
		this.scriptDataId = scriptDataId;
	}

	public String getScriptId() {
		return scriptId;
	}

	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	public byte[] getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(byte[] scriptFile) {
		this.scriptFile = scriptFile;
	}

	public String getScriptFileName() {
		return scriptFileName;
	}

	public void setScriptFileName(String scriptFileName) {
		this.scriptFileName = scriptFileName;
	}

	public Timestamp getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	
	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getScriptFileText() {
		return scriptFileText;
	}

	public void setScriptFileText(String scriptFileText) {
		this.scriptFileText = scriptFileText;
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		JSONObject innerObject = new JSONObject();
		
		if( scriptFile != null && scriptFile.length > 0)
			jsonObject.put(scriptFileName, new String(scriptFile));
		
		innerObject.put(scriptFileName, jsonObject);
		return innerObject;
	}
}