package com.elitecore.netvertexsm.ws.cxfws.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class LongToStringAdapter extends XmlAdapter<String, Long> {

	@Override
	public Long unmarshal(String v) throws Exception {
		
		if(v == null || v.trim().isEmpty() == true){
			return null;
		}
		
		try{
			return Long.parseLong(v.trim());
		}catch(Exception ex){
			throw ex;
		}
	}

	@Override
	public String marshal(Long v) throws Exception {
		if(v == null){
			return "";
		}
		return v.toString();
	}

	
	

}

