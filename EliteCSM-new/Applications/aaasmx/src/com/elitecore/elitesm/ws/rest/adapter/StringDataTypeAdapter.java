package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringDataTypeAdapter extends XmlAdapter<String, Long>{

	private static final String STRING = "String";

	@Override
	public String marshal(Long dataTypeId) throws Exception {
		String dataTypeName = null;
		if(0 == dataTypeId.intValue()){
			dataTypeName = STRING;
		}
		return dataTypeName;
	}

	@Override
	public Long unmarshal(String dataTypeName) throws Exception {
		Long dataTypeId = null;
		if(dataTypeName.equalsIgnoreCase(STRING)){
			dataTypeId = 0l;
		} else {
			dataTypeId = -1l;
		}
		return dataTypeId;
	}

}
