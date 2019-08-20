package com.elitecore.elitesm.datamanager.radius.bwlist.data;

import java.io.Serializable;
import java.text.ParseException;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.logger.Logger;

public class BWListData extends BaseData implements Serializable, Differentiable {

	private static final long serialVersionUID = 1L;
	private String bwId;
	private String attributeId;
	private String attributeValue;
	private String typeId;
	private String commonStatusId;
	private java.sql.Timestamp  validity;
	private String typeName;
	private String strValidity;
	private Integer orderNumber;
	private String auditUid; 
	
	public String getCommonStatusId() {
		return commonStatusId;
	}

	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}

	public String getBwId() {
		return bwId;
	}
	
	public void setBwId(String bwId) {
		this.bwId = bwId;
	}
	
	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public java.sql.Timestamp getValidity() {
		return validity;
	}

	public void setValidity(java.sql.Timestamp validity) {
		this.validity = validity;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getStrValidity() {
		return strValidity;
	}

	public void setStrValidity(String strValidity) {
		this.strValidity = strValidity;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getAuditUid() {
		return auditUid;
	}

	public void setAuditUid(String auditUid) {
		this.auditUid = auditUid;
	}

	@Override
	public JSONObject toJson() {
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("Attribute Id", attributeId);
		jsonObject.put("Attribute Value", attributeValue);
		
		try {
			jsonObject.put("Validity", EliteUtility.getValidity(validity));
		} catch (ParseException e) {
			Logger.logTrace(ConfigConstant.BLACKLIST_CANDIDATES_LABEL, e);
		}
		
		jsonObject.put("Type", (typeName.equals("B") ? "Blacklist" : "Whitelist"));
		
		return jsonObject;
	}
}
