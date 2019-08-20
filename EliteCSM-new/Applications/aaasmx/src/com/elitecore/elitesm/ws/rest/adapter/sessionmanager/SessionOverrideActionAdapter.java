package com.elitecore.elitesm.ws.rest.adapter.sessionmanager;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

public class SessionOverrideActionAdapter extends XmlAdapter<String, Integer>{

	@Override
	public String marshal(Integer sessionOverrideActionId) throws Exception {
		String sessionOverrideActionName = null;
		try {
			if(sessionOverrideActionId == 3){
				sessionOverrideActionName = "None";
			} else if(sessionOverrideActionId == 1){
				sessionOverrideActionName = "Generate Disconnect";
			} else if(sessionOverrideActionId == 2){
				sessionOverrideActionName = "Generate Stop";
			} else if(sessionOverrideActionId == 4){
				sessionOverrideActionName = "Generate Disconnect and Stop";
			}

		} catch(Exception e){
			sessionOverrideActionName = " ";
		}
		return sessionOverrideActionName;
	}

	@Override
	public Integer unmarshal(String sessionOverrideActionName) throws Exception {

		Integer sessionOverrideActionId = null;
		if(Strings.isNullOrEmpty(sessionOverrideActionName) == false){

			try {
				if(sessionOverrideActionName.equalsIgnoreCase("None")){
					sessionOverrideActionId = 3;
				} else if(sessionOverrideActionName.equalsIgnoreCase("Generate Disconnect")){
					sessionOverrideActionId = 1;
				} else if(sessionOverrideActionName.equalsIgnoreCase("Generate Stop")){
					sessionOverrideActionId = 2;
				} else if(sessionOverrideActionName.equalsIgnoreCase("Generate Disconnect and Stop")){
					sessionOverrideActionId = 4;
				} else {
					sessionOverrideActionId = -1;
				}

			} catch (Exception e) {
				sessionOverrideActionId = -1;
			}
		} else {
			sessionOverrideActionId = -1;
		}
		return sessionOverrideActionId;
	}
}
