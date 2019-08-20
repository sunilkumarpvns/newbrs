package com.elitecore.elitesm.ws.rest.adapter.eapconfig;

import javax.xml.bind.annotation.adapters.XmlAdapter;


import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
public class EAPAuthMethodsAdapter extends XmlAdapter<String, String>{
	
	@Override
	public String unmarshal(String eapAuthMethods) {
		String eapAuthMethodIds="";
		try {
			if(Strings.isNullOrBlank(eapAuthMethods) == false){
				eapAuthMethodIds = EAPConfigUtils.convertEnableAuthMethodToAuthTypeId(eapAuthMethods,false);
			}
		} catch (Exception e){
			eapAuthMethodIds="";
		}
		return eapAuthMethodIds;
	}

	@Override
	public String marshal(String eapAuthMethodIds) {
		String eapAuthMethodNames = "";
		try {
			if(Strings.isNullOrBlank(eapAuthMethodIds) == false){
				eapAuthMethodNames = EAPConfigUtils.convertEnableAuthMethodToLabelString(eapAuthMethodIds);
			}
		} catch(Exception e) {
			eapAuthMethodNames = "";
		}
		return eapAuthMethodNames;
	}

}
