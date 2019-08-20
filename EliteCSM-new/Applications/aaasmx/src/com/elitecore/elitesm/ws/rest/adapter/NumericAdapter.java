package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class NumericAdapter extends XmlAdapter<String, Long>{

	@Override
	public Long unmarshal(String val) throws Exception {
		try{
			return Long.parseLong(val.trim());
		}	catch(Exception e){
			return -1l;
		}	
	}

	@Override
	public String marshal(Long value) throws Exception {
		String val = String.valueOf(value);
		return val.trim();
	}
}
