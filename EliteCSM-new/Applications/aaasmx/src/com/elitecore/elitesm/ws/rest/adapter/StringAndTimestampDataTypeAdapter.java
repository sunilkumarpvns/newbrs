package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

public class StringAndTimestampDataTypeAdapter extends XmlAdapter<String, Integer>{

	@Override
	public String marshal(Integer dataTypeId) throws Exception {
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
	public Integer unmarshal(String dataTypeName) throws Exception {

		Integer dataTypeId = null;
		if(Strings.isNullOrEmpty(dataTypeName) == false){

			try {
				if(dataTypeName.equalsIgnoreCase("String")){
					dataTypeId = 0;
				} else if(dataTypeName.equalsIgnoreCase("Timestamp")){
					dataTypeId = 1;
				} else {
					dataTypeId = -1;
				}

			} catch (Exception e) {
				dataTypeId = -1;
			}
		} else {
			dataTypeId = -1;
		}
		return dataTypeId;
	}
}
