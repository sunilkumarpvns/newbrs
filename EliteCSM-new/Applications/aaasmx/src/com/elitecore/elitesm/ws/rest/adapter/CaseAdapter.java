package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CaseAdapter extends XmlAdapter<String, Integer> {

	private final static String UPPER_CASE = "Upper Case";
	private final static String LOWER_CASE = "Lower Case";
	private static final String NONE = "No Change";
	
	@Override
	public Integer unmarshal(String v) throws Exception {
		if (UPPER_CASE.equalsIgnoreCase(v)) {
			return new Integer(3);
		} else if (LOWER_CASE.equalsIgnoreCase(v)) {
			return new Integer(2);
		} else if (NONE.equalsIgnoreCase(v)) {
			return new Integer(1);
		}
		return null;
	}

	@Override
	public String marshal(Integer v) throws Exception {
		if (v == 3) {
			return UPPER_CASE;
		} else if (v == 2) {
			return LOWER_CASE;
		} else if (v == 1) {
			return NONE;
		}
		return "";
	}
}
