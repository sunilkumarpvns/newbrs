package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RequestModeAdapter extends XmlAdapter<String, Integer> {

	public final static String AUTHENTICATE_ONLY = "Authenticate-Only";
	public final static String AUTHORIZE_ONLY = "Authorize-Only";
	public final static String AUTHENTICATE_AND_AUTHORIZE = "Authenticate and Authorize";
	
	@Override
	public Integer unmarshal(String v) throws Exception {
		if (AUTHENTICATE_ONLY.equalsIgnoreCase(v)) {
			return new Integer(1);
		} else if (AUTHORIZE_ONLY.equalsIgnoreCase(v)) {
			return new Integer(2);
		} else if (AUTHENTICATE_AND_AUTHORIZE.equalsIgnoreCase(v)) {
			return new Integer(3);
		}
		return null;
	}

	@Override
	public String marshal(Integer v) throws Exception {
		if (v == 1) {
			return AUTHENTICATE_ONLY;
		} else if (v == 2) {
			return AUTHORIZE_ONLY;
		} else if (v ==3) {
			return AUTHENTICATE_AND_AUTHORIZE;
		}
		return "";
	}
}
