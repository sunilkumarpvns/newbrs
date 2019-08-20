package com.elitecore.elitesm.ws.rest.adapter.sessionmanager;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

public class BehaviorAdapter extends XmlAdapter<String, Integer>{

	@Override
	public String marshal(Integer behaviorId) throws Exception {
		String behaviorName = null;
		try {
			if(behaviorId == 1){
				behaviorName = "Acct";
			} else if(behaviorId == 2){
				behaviorName = "Auth";
			}

		} catch(Exception e){
			behaviorName = " ";
		}
		return behaviorName;
	}

	@Override
	public Integer unmarshal(String behaviorName) throws Exception {
		Integer behaviorId = null;
		if(Strings.isNullOrEmpty(behaviorName) == false){

			try {
				if(behaviorName.equalsIgnoreCase("Acct")){
					behaviorId = 1;
				} else if(behaviorName.equalsIgnoreCase("Auth")){
					behaviorId = 2;
				} else {
					behaviorId = -1;
				}

			} catch (Exception e) {
				behaviorId = -1;
			}
		} else {
			behaviorId = -1;
		}
		return behaviorId;
	}
}
