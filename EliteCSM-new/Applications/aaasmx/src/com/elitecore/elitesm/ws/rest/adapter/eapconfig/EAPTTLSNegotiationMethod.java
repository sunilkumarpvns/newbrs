package com.elitecore.elitesm.ws.rest.adapter.eapconfig;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;

public class EAPTTLSNegotiationMethod extends XmlAdapter<String, Integer>{

	@Override
	public Integer unmarshal(String ttlsNegotiationMethodName) throws Exception {
		Integer ttlsNegotiationMethodId = null;
		ttlsNegotiationMethodId = EAPConfigUtils.convertEapTTLSNegotiationMethodStringToId(ttlsNegotiationMethodName);
		if(ttlsNegotiationMethodId == null){
			return -1;
		}
		return ttlsNegotiationMethodId;
	}

	@Override
	public String marshal(Integer ttlsNegotiationMethodId) throws Exception {
		String ttlsNegotiationMethodName="";
		if(ttlsNegotiationMethodId != null){
			ttlsNegotiationMethodName = EAPConfigUtils.convertEapTTLSNegotiationMethodToString(ttlsNegotiationMethodId);
		}
		return ttlsNegotiationMethodName;
	}

}
