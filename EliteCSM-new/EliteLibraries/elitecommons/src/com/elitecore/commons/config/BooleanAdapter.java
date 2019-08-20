package com.elitecore.commons.config;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

/**
 * Custom boolean adapter that returns true for "true" or "yes", ignoring case, false otherwise.
 *   
 * @author narendra.pathai
 *
 */
public class BooleanAdapter extends XmlAdapter<String, Boolean>{

	@Override
	public Boolean unmarshal(String strBoolean) throws Exception {
		return Strings.toBoolean(strBoolean);
	}

	@Override
	public String marshal(Boolean bool) throws Exception {
		return bool != null ? String.valueOf(bool) : "false";
	}
}