package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.TimeBoundry;

public class TimeBoundryAdapter extends XmlAdapter<String, Long>{

	private static final String EMPTY = "";

	@Override
	public String marshal(Long timeBoundry) throws Exception {
		
		String timeBoundryName = null;
		
		for (TimeBoundry timeBoundryVal : TimeBoundry.values()) {
			if(timeBoundryVal.id ==  timeBoundry){
				timeBoundryName = timeBoundryVal.name;
			}
		} 
		return timeBoundryName;
	}

	@Override
	public Long unmarshal(String timeBoundryName) throws Exception {
		
		Long timeBoundry = null;
		
		if(EMPTY.equalsIgnoreCase(timeBoundryName)){
			return null;
		}
		
		for (TimeBoundry timeBoundryVal : TimeBoundry.values()) {
			if(timeBoundryVal.name.equalsIgnoreCase(timeBoundryName)){
				timeBoundry = timeBoundryVal.id;
			}
		}
		if(timeBoundry == null){
			return -1l;
		}
		return timeBoundry;
	}
}
