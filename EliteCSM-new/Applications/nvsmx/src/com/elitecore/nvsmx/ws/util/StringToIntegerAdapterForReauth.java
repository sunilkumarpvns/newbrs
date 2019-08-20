package com.elitecore.nvsmx.ws.util;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

public class StringToIntegerAdapterForReauth extends XmlAdapter<String, Integer> {

	private static final String MODULE = "STR-INTEGER-ADP";

	@Override
	public Integer unmarshal(String v) throws Exception {
		
		if (Strings.isNullOrBlank(v)) {
			getLogger().warn(MODULE, "Empty value received for parameter 'updateAction'");
			return null;
		}

		try{
			return Integer.parseInt(v.trim());
			
		}catch(Exception ex){
			getLogger().warn(MODULE, "Invalid Integer value '" + v + "' for parameter 'updateAction', Accepted values are only 0/1/2");
			return null;
		}
 	}

	@Override
	public String marshal(Integer v) throws Exception {
		if (v == null) {
			return "";
		} else {
			return v.toString();
		}
	}

}
