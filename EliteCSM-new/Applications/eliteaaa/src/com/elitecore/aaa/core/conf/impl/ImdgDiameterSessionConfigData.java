package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@XmlRootElement(name="imdg-diameter-data")
@XmlType(propOrder = {})
public class ImdgDiameterSessionConfigData implements Differentiable {

	//Diameter Field And Attribute Mapping List
	private List<ImdgIndexDetail> diameterIMDGFieldMapping;

	public ImdgDiameterSessionConfigData() {
		this.diameterIMDGFieldMapping = new ArrayList<>();
	}

	@XmlElementWrapper(name="diameter-imdg-mapping-list")
	@XmlElement(name="imdg-mapping")
	public List<ImdgIndexDetail> getDiameterIMDGFieldMapping() {
		return diameterIMDGFieldMapping;
	}

	public void setDiameterIMDGFieldMapping(List<ImdgIndexDetail> diameterIMDGFieldMapping) {
		this.diameterIMDGFieldMapping = diameterIMDGFieldMapping;
	}

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();

		if(Collectionz.isNullOrEmpty(diameterIMDGFieldMapping) == false){
			JSONArray jsonArray = new JSONArray();
			for (ImdgIndexDetail diameterField : diameterIMDGFieldMapping) {
				jsonArray.add(diameterField.toJson());
			}
			jsonObject.put("Diameter Session Field Mapping", jsonArray);
		}
		return jsonObject;
	}
}
