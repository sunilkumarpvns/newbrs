
package com.elitecore.corenetvertex.pd.service;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity(name= "com.elitecore.corenetvertex.pd.service.ServiceData")
@Table(name = "	TBLM_SERVICE")
public class ServiceData extends DefaultGroupResourceData implements Serializable {

	private String name;
	private String description;

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@JsonIgnore
	@Transient
	@Override
	public String getResourceName() {
		return getName();
	}
	
	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.ALIAS, getId());
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());

		return jsonObject;
	}

	@Transient
	@JsonIgnore
	public String getServiceId() {
		return super.getId();
	}

	public void setServiceId(String serviceId) {
		super.setId(serviceId);
	}
}