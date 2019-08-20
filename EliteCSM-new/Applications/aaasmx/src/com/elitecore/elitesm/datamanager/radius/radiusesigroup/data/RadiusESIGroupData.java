package com.elitecore.elitesm.datamanager.radius.radiusesigroup.data;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

import net.sf.json.JSONObject;

import java.util.List;

public class RadiusESIGroupData extends BaseData implements Differentiable{
	
	private String id;

	private String name;

	private String description;

	private  byte esiGroupDataXml[];

	private String auditUId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RadiusESIGroupData() {
		description = RestUtitlity.getDefaultDescription();
	}

	public byte[] getEsiGroupDataXml() {
		return esiGroupDataXml;
	}

	public void setEsiGroupDataXml(byte[] esiGroupDataXml) {
		this.esiGroupDataXml = esiGroupDataXml;
	}

	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@Override
	public JSONObject toJson() {
		// TODO Auto-generated method stub
		JSONObject object = new JSONObject();
		object.put("ESI Group Name", name);
		object.put("Description", description);

		return object;
	}
}
