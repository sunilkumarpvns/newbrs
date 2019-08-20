package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntegerValidatorAdaptor extends XmlAdapter<String, Integer>{

	@Override
	public Integer unmarshal(String val) {
		try {
			Integer value =  Integer.parseInt(val.trim());
			return value;
		} catch (NumberFormatException e) {
			return -2;
		}
	}

	@Override
	public String marshal(Integer value) throws Exception {
		String val = String.valueOf(value);
		return val.trim();
	}

}
