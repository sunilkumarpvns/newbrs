package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.ws.rest.adapter.sessionmanager.SessionManagerESIAdaptor;

public class SMSessionCloserESIRelData extends BaseData implements Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;
	private String smConfigId;
	private String esiInstanceId;
	
	@NotNull(message="Weightage for ESI must be specified")
	private Integer weightage;
	
	private ExternalSystemInterfaceInstanceData	externalSystemData;
	
	@XmlTransient
	public String getSmConfigId() {
		return smConfigId;
	}
	public void setSmConfigId(String smConfigId) {
		this.smConfigId = smConfigId;
	}
	
	@XmlElement(name="esi-name")
	@NotEmpty(message = "Session manager esi name must be specified")
	@XmlJavaTypeAdapter(value = SessionManagerESIAdaptor.class)
	public String getEsiInstanceId() {
		return esiInstanceId;
	}
	public void setEsiInstanceId(String esiInstanceId) {
		this.esiInstanceId = esiInstanceId;
	}
	
	@XmlElement(name="weightage")
	@Min(value = 0 , message = "Weightage for ESI must be valid")
	public Integer getWeightage() {
		return weightage;
	}
	public void setWeightage(Integer weightage) {
		this.weightage = weightage;
	}
	
	@XmlTransient
	public ExternalSystemInterfaceInstanceData getExternalSystemData() {
		return externalSystemData;
	}
	public void setExternalSystemData(
			ExternalSystemInterfaceInstanceData externalSystemData) {
		this.externalSystemData = externalSystemData;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object=new JSONObject();
		object.put("Name", externalSystemData.getName() +"-W-"+weightage);
		return object;
	}
	
}
