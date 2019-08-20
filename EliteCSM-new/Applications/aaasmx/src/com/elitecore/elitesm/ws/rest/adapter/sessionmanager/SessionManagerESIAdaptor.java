package com.elitecore.elitesm.ws.rest.adapter.sessionmanager;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

public class SessionManagerESIAdaptor extends XmlAdapter<String, String>{

	@Override
	public String marshal(String esiId) {
		String esiName = null;
		try {
			ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();
			esiName = externalSystemInterfaceBLManager.getExternalSystemnameFromId(esiId);
		} catch (Exception e) {
			esiName = "";
		}
		return esiName;
	}

	@Override
	public String unmarshal(String esiName) {
		
		String esiId = null;
			ExternalSystemInterfaceInstanceData externalSystemInterfaceInstanceData = new ExternalSystemInterfaceInstanceData();
			ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();
			
			try {
				if(Strings.isNullOrBlank(esiName) == false){
					externalSystemInterfaceInstanceData = externalSystemInterfaceBLManager.getExternalSystemInterfaceInstanceDataByName(esiName);
					esiId = externalSystemInterfaceInstanceData.getEsiInstanceId();
				}
			} catch (Exception e) {
				esiId = RestValidationMessages.INVALID;
		}
		return esiId;
	}

	
}
