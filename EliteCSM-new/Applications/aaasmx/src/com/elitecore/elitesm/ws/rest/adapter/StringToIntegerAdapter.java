package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringToIntegerAdapter extends XmlAdapter<String, Integer>{

	@Override
	public Integer unmarshal(String val) throws Exception {
		try{
			Integer value =  Integer.parseInt(val.trim());
			return value;
		}	catch(Exception e){
			return -1;
		}	
	}

	@Override
	public String marshal(Integer value) throws Exception {
		String val = String.valueOf(value);
		return val.trim();
	}
}
