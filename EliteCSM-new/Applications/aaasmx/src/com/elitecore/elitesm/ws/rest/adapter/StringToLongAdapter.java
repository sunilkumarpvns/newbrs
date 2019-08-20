package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

public class StringToLongAdapter extends XmlAdapter<String, Long>{

	@Override
	public Long unmarshal(String value) throws Exception {
		Long longValue = null;
		if(Strings.isNullOrEmpty(value) == false){

			try {
				longValue = Long.parseLong(value);
			} catch (Exception e) {
				longValue = -1l;
			}
		} else {
			longValue = null;
		}
		return longValue;
	}

	@Override
	public String marshal(Long value) throws Exception {
		String val = String.valueOf(value);
		return val.trim();
	}

}
