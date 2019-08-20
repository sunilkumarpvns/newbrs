package com.elitecore.elitesm.datamanager.servermgr.plugins.quotamgrplugin.data;

import java.io.Serializable;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuotaMgtPluginData extends BaseData implements IQuotaMgrPluginData,Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;
	private String pluginId;
	
	@Expose
	@SerializedName("Plugin Instance Id")
	private String pluginInstanceId;
	
	@Expose
	@SerializedName("pluginData")
	private byte[] pluginData;

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getPluginInstanceId() {
		return pluginInstanceId;
	}

	public void setPluginInstanceId(String pluginInstanceId) {
		this.pluginInstanceId = pluginInstanceId;
	}

	public byte[] getPluginData() {
		return pluginData;
	}

	public void setPluginData(byte[] pluginData) {
		this.pluginData = pluginData;
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		if( pluginData != null )
			jsonObject.put("File Data", new String(pluginData));
		return jsonObject;
	}
}