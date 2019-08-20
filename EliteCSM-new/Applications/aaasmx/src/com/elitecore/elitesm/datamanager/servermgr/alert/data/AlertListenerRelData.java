package com.elitecore.elitesm.datamanager.servermgr.alert.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;

public class AlertListenerRelData extends BaseData implements Serializable,Differentiable{
	
	private String instanceId;
	private String typeId;
	private AlertListenerData alertListenerData;
	
	@XmlTransient
	public String getInstanceId() {
		return instanceId;
	}
	
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	@XmlElement(name = "type-id")
	public String getTypeId() {
		return typeId;
	}
	
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
	@XmlTransient
	public AlertListenerData getAlertListenerData() {
		return alertListenerData;
	}
	
	public void setAlertListenerData(AlertListenerData alertListenerData) {
		this.alertListenerData = alertListenerData;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object=new JSONObject();
		object.put("Type Id", EliteSMReferencialDAO.fetchAlertTypeData(typeId));
		return object;
	}
	
	
}
