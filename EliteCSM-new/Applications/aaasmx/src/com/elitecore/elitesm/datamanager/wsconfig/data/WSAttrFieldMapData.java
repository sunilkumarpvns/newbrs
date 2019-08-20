/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SsubscriberdbfieldmapData.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.wsconfig.data;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

import net.sf.json.JSONObject;

public class WSAttrFieldMapData extends BaseData implements IWSAttrFieldMapData, Differentiable{

	private String attrFieldMapId;
	private String wsConfigId;
	private String attribute;
	private String fieldName;
	private Integer orderNumber;
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return the attrFieldMapId
	 */
	public String getAttrFieldMapId() {
		return attrFieldMapId;
	}
	/**
	 * @param attrFieldMapId the attrFieldMapId to set
	 */
	public void setAttrFieldMapId(String attrFieldMapId) {
		this.attrFieldMapId = attrFieldMapId;
	}
	/**
	 * @return the wsConfigId
	 */
	public String getWsConfigId() {
		return wsConfigId;
	}
	/**
	 * @param wsConfigId the wsConfigId to set
	 */
	public void setWsConfigId(String wsConfigId) {
		this.wsConfigId = wsConfigId;
	}
	
	
	
	
	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}
	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Field", fieldName);
		return object;
	}
}
