package com.elitecore.elitesm.ws.rest.adapter.eapconfig;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
/**
 * 
 * @author Chirag.i.prajapati
 *
 */
public class EAPServerCertificateAdapter extends XmlAdapter<String, String>{
	EAPConfigBLManager eapBLManager = new EAPConfigBLManager();
	@Override
	public String unmarshal(String serverCertificateName){
		if(Strings.isNullOrBlank(serverCertificateName)){
			return null;
		}else if(RestValidationMessages.NONE.equalsIgnoreCase(serverCertificateName) || "0".equals(serverCertificateName)){
			return null;
		}
		
		String serverCertifiacateId;
		try{
			serverCertifiacateId = eapBLManager.getServerCertificateIdFromName(serverCertificateName);
			
			if(serverCertifiacateId == null){
				serverCertifiacateId = RestValidationMessages.INVALID;
			}
		}catch(Exception e){
			serverCertifiacateId = RestValidationMessages.INVALID;
		}
		return serverCertifiacateId;
	}

	@Override
	public String marshal(String serverCertificateId) {
		if(Strings.isNullOrBlank(serverCertificateId)){
			return RestValidationMessages.NONE;
		}
		String serverCertifiacateName = null;
		try{
			serverCertifiacateName = eapBLManager.getServerCertificateNameFromId(serverCertificateId);
			
		}catch(Exception e){
			return RestValidationMessages.NONE;
		}
		return serverCertifiacateName;
	}
	
}
