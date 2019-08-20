package com.elitecore.elitesm.ws.rest.adapter.eapconfig;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;

/**
 * 
 * @author Chirag.i.prajapati
 *
 */
public class EAPDefaultNegotiationMethodAdapter  extends XmlAdapter<String, Long>{
	
	@Override
	public Long unmarshal(String defaultNegotiationMethod) throws Exception {
		String eapAuthMethodIds="";
		Long methodId ;
		try{
			if(Strings.isNullOrBlank(defaultNegotiationMethod) == false){
				eapAuthMethodIds = EAPConfigUtils.convertEnableAuthMethodToAuthTypeId(defaultNegotiationMethod,true);
			}
			methodId=Long.parseLong(eapAuthMethodIds);
			if(methodId == null){
				methodId= -1L;
			}
			
		} catch(Exception e){
			return -1L;
		}
		
		return methodId;
	}

	@Override
	public String marshal(Long defaultNegotiationMethodId) throws Exception {
		String defaultNegotiationMethod="";
		try {
			if(defaultNegotiationMethodId != null){
				defaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(defaultNegotiationMethodId);
			}			
		} catch (Exception e){
			defaultNegotiationMethod = "";
		}
		return defaultNegotiationMethod;
	}
}
