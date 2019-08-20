package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringValidatorAdaptor extends XmlAdapter<String, String>{

	@Override
	public String unmarshal(String valueToBeUnmarshal) throws Exception {
		
		String marshaledValue = null;
		try {
			 Integer.parseInt(valueToBeUnmarshal.trim());
				marshaledValue =  valueToBeUnmarshal;
		} catch (Exception e) {
			marshaledValue = "INVALID";
		}
		return marshaledValue;
	}

	@Override
	public String marshal(String value) throws Exception {
		
		String val = String.valueOf(value);
		return val.trim();
	}

}
