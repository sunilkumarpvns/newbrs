package com.elitecore.elitesm.datamanager.servermgr.alert.data;

import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class BaseAlertListener extends BaseData implements Differentiable{
	private String listenerId;
	
	@XmlTransient
	public String getListenerId() {
		return listenerId;
	}

	public void setListenerId(String listenerId) {
		this.listenerId = listenerId;
	}

	@Override
	public JSONObject toJson() {
		return null;
	}

}
