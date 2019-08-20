package com.elitecore.elitesm.ws.rest.adapter.eapconfig;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPTLSConfigData;

public class TLSCipherSuitesAdapter extends XmlAdapter<String, String> {
	
	@Override
	public String unmarshal(String cipherSuiteNames) throws Exception {
		String cipherSuiteIds = "";
		if(Strings.isNullOrBlank(cipherSuiteNames) == false){
			cipherSuiteIds = EAPTLSConfigData.convertCipherSuitesNamesToCipherSuiteCodes(cipherSuiteNames);
		}
		return cipherSuiteIds;
	}

	@Override
	public String marshal(String cipherSuitesIds) throws Exception {
		String cipherSuiteNames = "";
		if(Strings.isNullOrBlank(cipherSuitesIds) == false){
			cipherSuiteNames = EAPTLSConfigData.convertCipherSuiteCodeToCipherSuiteName(cipherSuitesIds);
		}
		return cipherSuiteNames;
	}
	
}
