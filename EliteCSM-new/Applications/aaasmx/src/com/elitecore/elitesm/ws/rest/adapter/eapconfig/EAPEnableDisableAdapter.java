package com.elitecore.elitesm.ws.rest.adapter.eapconfig;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.EliteUtility;

public class EAPEnableDisableAdapter extends XmlAdapter<String, String>{

	@Override
	public String unmarshal(String value) {
		try{
			return value.toUpperCase();
			
		} catch(Exception e){
			return "";
		}
	}

	@Override
	public String marshal(String value){
		try {
			
			return EliteUtility.getCapitalizeString(value);
		} catch(Exception e){
			return "";
		}
		
	}
	
}
