/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyData.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.sessionmanager.data;

import java.io.Serializable;
import java.util.Set;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

@XmlType(propOrder = {"mappingName","sessionManagerFieldMappingData"})
public class DiameterSessionManagerMappingData extends BaseData implements Differentiable,Serializable{

		private static final long serialVersionUID = 1L;
		private String mappingId;
		
		@NotEmpty(message = "Mapping Name must be Specified")
	    private String mappingName;
	    private String sessionManagerId;
	    private Integer orderNumber;
	    
	    @Valid
	    private Set<SessionManagerFieldMappingData> sessionManagerFieldMappingData;
	    
	    @XmlTransient
		public String getMappingId() {
			return mappingId;
		}
		public void setMappingId(String mappingId) {
			this.mappingId = mappingId;
		}
		
		@XmlElement(name = "mapping-name")
		public String getMappingName() {
			return mappingName;
		}
		public void setMappingName(String mappingName) {
			this.mappingName = mappingName;
		}
		
		@XmlTransient
		public String getSessionManagerId() {
			return sessionManagerId;
		}
		public void setSessionManagerId(String sessionManagerId) {
			this.sessionManagerId = sessionManagerId;
		}

		@XmlElement(name = "field-mapping-data")
		public Set<SessionManagerFieldMappingData> getSessionManagerFieldMappingData() {
			return sessionManagerFieldMappingData;
		}
		public void setSessionManagerFieldMappingData(
				Set<SessionManagerFieldMappingData> sessionManagerFieldMappingData) {
			this.sessionManagerFieldMappingData = sessionManagerFieldMappingData;
		}
		
		@Override
		public JSONObject toJson() {
			JSONObject object = new JSONObject();
			object.put("Mapping Name", mappingName);
		
			// Field Mappings
			if (sessionManagerFieldMappingData != null) {
				JSONArray jsonArray = new  JSONArray();
				for (SessionManagerFieldMappingData element : sessionManagerFieldMappingData) {
					jsonArray.add(element.toJson());
				}
				object.put("DB Field Mappings ", jsonArray);
			}
			return object;
		}
		
		@XmlTransient
		public Integer getOrderNumber() {
			return orderNumber;
		}
		public void setOrderNumber(Integer orderNumber) {
			this.orderNumber = orderNumber;
		}
}
