package com.elitecore.config.core;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanAdapter extends XmlAdapter<String, Boolean>{

	@Override
	public Boolean unmarshal(String strBoolean) throws Exception {
		return Boolean.parseBoolean(strBoolean);
	}

	@Override
	public String marshal(Boolean bool) throws Exception {
		return String.valueOf(bool);
	}

}
