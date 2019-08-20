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

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

import net.sf.json.JSONObject;

public class WSDBFieldMapData extends BaseData implements IWSDBFieldMapData{

	private String dbfieldId;
	private String wsConfigId;
	private String key;
	private String fieldName;
	private Integer orderNumber ;
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return the dbfieldId
	 */
	public String getDbfieldId() {
		return dbfieldId;
	}
	/**
	 * @param dbfieldId the dbfieldId to set
	 */
	public void setDbfieldId(String dbfieldId) {
		this.dbfieldId = dbfieldId;
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
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
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
