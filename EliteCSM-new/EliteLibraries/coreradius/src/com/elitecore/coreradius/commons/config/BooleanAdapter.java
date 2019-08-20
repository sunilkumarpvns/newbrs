package com.elitecore.coreradius.commons.config;


import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Custom boolean adapter that returns true for "true" or "yes", ignoring case, false otherwise
 *   
 * @author narendra.pathai
 *
 */
public class BooleanAdapter extends XmlAdapter<String, Boolean>{

	@Override
	public Boolean unmarshal(String strBoolean) throws Exception {
		return (strBoolean != null) && 
		(strBoolean.equalsIgnoreCase("true") 
		|| strBoolean.equalsIgnoreCase("yes"));
	}

	@Override
	public String marshal(Boolean bool) throws Exception {
		return bool != null ? String.valueOf(bool) : "false";
	}
}
