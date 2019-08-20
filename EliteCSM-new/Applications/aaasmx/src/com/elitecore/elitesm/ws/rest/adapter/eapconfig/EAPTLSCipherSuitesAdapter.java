package com.elitecore.elitesm.ws.rest.adapter.eapconfig;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;

public class EAPTLSCipherSuitesAdapter extends XmlAdapter<String, String>{

	@Override
	public String unmarshal(String cipherSuiteNames) throws Exception {
		String cipherSuiteIds = "";
		if(Strings.isNullOrBlank(cipherSuiteNames) == false){
			cipherSuiteIds = EAPConfigUtils.convertCipherSuitesNamesToCipherSuiteCodes(cipherSuiteNames);
		}
		return cipherSuiteIds;
	}

	@Override
	public String marshal(String cipherSuitesIds) throws Exception {
		String cipherSuiteNames = "";
		if(Strings.isNullOrBlank(cipherSuitesIds) == false){
			cipherSuiteNames = EAPConfigUtils.convertCipherSuiteCodeToCipherSuiteName(cipherSuitesIds);
		}
		return cipherSuiteNames;
	}
	
}
