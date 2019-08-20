package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

public class LDAPDatasourceVersionAdapter extends XmlAdapter<String, Integer>{

	@Override
	public String marshal(Integer versionId) throws Exception {
		String versionName = null;
		try {
			if(versionId == 2){
				versionName = "2";
			} else if(versionId == 3){
				versionName = "3";
			}
		} catch(Exception e){
			versionName = " ";
		}
		return versionName;
	}

	@Override
	public Integer unmarshal(String versionName) throws Exception {
		Integer versionId = null;
		if(Strings.isNullOrEmpty(versionName) == false){

			try {
				versionId = Integer.parseInt(versionName);
			} catch (Exception e) {
				versionId = -1;
			}
		} else {
			versionId = null;
		}
		return versionId;
	}
}

