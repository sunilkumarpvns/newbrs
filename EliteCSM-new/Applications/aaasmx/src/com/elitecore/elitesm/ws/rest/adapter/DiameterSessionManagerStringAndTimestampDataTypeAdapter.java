package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

public class DiameterSessionManagerStringAndTimestampDataTypeAdapter extends XmlAdapter<String, Long> {
	@Override
	public String marshal(Long dataTypeId) throws Exception {
		String dataTypeName = null;
		try {
			if(dataTypeId == 0){
				dataTypeName = "String";
			} else if(dataTypeId == 1){
				dataTypeName = "Timestamp";
			}

		} catch(Exception e){
			dataTypeName = " ";
		}
		return dataTypeName;
	}

	@Override
	public Long unmarshal(String dataTypeName) throws Exception {

		if(Strings.isNullOrEmpty(dataTypeName)){
			return null;
		}
		
		Long dataTypeId = null;
		if(Strings.isNullOrEmpty(dataTypeName) == false){

			try {
				if(dataTypeName.equalsIgnoreCase("String")){
					dataTypeId = 0l;
				} else if(dataTypeName.equalsIgnoreCase("Timestamp")){
					dataTypeId = 1l;
				} else {
					dataTypeId = -1l;
				}

			} catch (Exception e) {
				dataTypeId = -1l;
			}
		} else {
			dataTypeId = -1l;
		}
		return dataTypeId;
	}
}
