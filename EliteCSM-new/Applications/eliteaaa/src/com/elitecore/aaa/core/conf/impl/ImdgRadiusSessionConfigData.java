package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@XmlRootElement(name="imdg-radius-data")
public class ImdgRadiusSessionConfigData  implements Differentiable {

	private static final String SEPERATER = ",|;";
	private List<ImdgIndexDetail> radiusIMDGFieldMapping;
	private List<RadiusSessionFieldMapping> radiusSessionFieldMappingList;
	private Map<String, ImdgIndexDetail> radiusIndexFieldMap;
	private RadiusInMemorySessionClosureAndOverrideProperties sessionClosureProperties;

	public ImdgRadiusSessionConfigData() {
		this.radiusIMDGFieldMapping = new ArrayList<>();
		this.radiusSessionFieldMappingList = new ArrayList<>();
		this.radiusIndexFieldMap = new HashMap<>(5);
	}

	@XmlElementWrapper(name="radius-imdg-mapping-list")
	@XmlElement(name="imdg-mapping")
	public List<ImdgIndexDetail> getRadiusIMDGFieldMapping() {
		return radiusIMDGFieldMapping;
	}

	public void setRadiusIMDGFieldMapping(List<ImdgIndexDetail> radiusIMDGFieldMapping) {
		this.radiusIMDGFieldMapping = radiusIMDGFieldMapping;
	}

	@XmlElementWrapper(name="radius-session-field-mapping-list")
	@XmlElement(name="radius-session-field-mapping")
	public List<RadiusSessionFieldMapping> getRadiusSessionFieldMappingList() {
		return radiusSessionFieldMappingList;
	}

	public void setRadiusSessionFieldMappingList(List<RadiusSessionFieldMapping> radiusSessionFieldMapping) {
		this.radiusSessionFieldMappingList = radiusSessionFieldMapping;
	}

	public void postRead() {
		for(ImdgIndexDetail imdgFieldMapping : radiusIMDGFieldMapping) {
			if(!Strings.isNullOrEmpty(imdgFieldMapping.getImdgAttributeValue())) {
				imdgFieldMapping.setAttributeList(new ArrayList<>(Arrays.asList(imdgFieldMapping.getImdgAttributeValue().split(SEPERATER))));
			}
			this.radiusIndexFieldMap.put(imdgFieldMapping.getImdgFieldValue(),imdgFieldMapping);
		}

		for(RadiusSessionFieldMapping fieldMapping : radiusSessionFieldMappingList) {
			if(!Strings.isNullOrEmpty(fieldMapping.getAttributes())) {
				fieldMapping.setAttributeList(new ArrayList<>(Arrays.asList(fieldMapping.getAttributes().split(SEPERATER))));
			}
		}
	}

	public Map<String, ImdgIndexDetail> getRadiusIndexFieldMap() {
		return radiusIndexFieldMap;
	}
	
	@XmlElement(name="session-closure-properties")
	public RadiusInMemorySessionClosureAndOverrideProperties getSessionClosureProperties() {
		return sessionClosureProperties;
	}

	public void setSessionClosureProperties(RadiusInMemorySessionClosureAndOverrideProperties sessionClosure) {
		this.sessionClosureProperties = sessionClosure;
	}

	public void setRadiusIndexFieldMap(Map<String, ImdgIndexDetail> radiusIndexFieldMap) {
		this.radiusIndexFieldMap = radiusIndexFieldMap;
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();

		if(Collectionz.isNullOrEmpty(radiusIMDGFieldMapping) == false){
			JSONArray jsonArray = new JSONArray();
			for (ImdgIndexDetail radiusField : radiusIMDGFieldMapping) {
				jsonArray.add(radiusField.toJson());
			}
			jsonObject.put("Radius Session Field Mapping", jsonArray);
		}
		jsonObject.put("Radius Session Closure Properties", sessionClosureProperties.toJson());
		return jsonObject;
	}
}
