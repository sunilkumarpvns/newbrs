package com.elitecore.corenetvertex.util.commons.jaxb.adapter;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LongToStringAdapter extends XmlAdapter<String, Long> {
	private static final String MODULE = "LONG-TO-STR-ADP";

	@Override
	public Long unmarshal(String str) throws Exception {
		if(str == null || str.trim().isEmpty() == true){
			return null;
		}
		try{
			return Long.parseLong(str.trim());
		}catch(Exception ex){ 
			getLogger().warn(MODULE, "Invalid Long value '" + str);
			ignoreTrace(ex);
			return Long.MIN_VALUE;
		}
	}

	@Override
	public String marshal(Long l) throws Exception {
		if(l == null){
			return "";
		}
		return l.toString();
	}
}
