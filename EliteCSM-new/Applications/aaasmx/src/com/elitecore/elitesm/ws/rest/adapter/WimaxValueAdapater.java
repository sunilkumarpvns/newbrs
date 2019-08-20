package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class WimaxValueAdapater extends XmlAdapter<String, String>{


	private static final String TRUE = "true";
	private static final String FALSE = "false";
	
	private final static String ENABLED = "Enabled";
	private final static String DISABLED = "Disabled";
	@Override
	public String unmarshal(String v) throws Exception {
		if (ENABLED.equalsIgnoreCase(v)) {
			return TRUE;
		} else if(DISABLED.equalsIgnoreCase(v)) {
			return FALSE;
		}
		return null;
	}

	@Override
	public String marshal(String v) throws Exception {
		return TRUE.equalsIgnoreCase(v) ? ENABLED : DISABLED;
	}

}
