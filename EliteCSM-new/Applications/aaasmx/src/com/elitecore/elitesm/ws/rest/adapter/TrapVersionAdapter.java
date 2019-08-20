package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

public class TrapVersionAdapter extends XmlAdapter<String, String>{

	private static final String ONE = "1";
	private static final String ZERO = "0";
	private static final String V2 = "V2";
	private static final String V2C = "V2c";
	private static final String V1 = "V1";

	@Override
	public String unmarshal(String trapVesion) throws Exception {
		String version = null;
		if (V1.equalsIgnoreCase(trapVesion)) {
			version =  ZERO;
		} else if (V2.equalsIgnoreCase(trapVesion) || V2C.equalsIgnoreCase(trapVesion) ) {
			version =  ONE;
		} else {
			version = RestValidationMessages.INVALID_VALUE;
		}
		return version;
	}

	@Override
	public String marshal(String trapVesion) throws Exception {
		String version = null;
		if (ZERO.equalsIgnoreCase(trapVesion)) {
			version =  V1;
		} else if (ONE.equalsIgnoreCase(trapVesion)) {
			version =  V2C;
		} 
		return version;
	}

}
