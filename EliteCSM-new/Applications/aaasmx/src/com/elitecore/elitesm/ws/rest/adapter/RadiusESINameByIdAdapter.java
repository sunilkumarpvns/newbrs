package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

public class RadiusESINameByIdAdapter extends XmlAdapter<String, String>{

	@Override
	public String unmarshal(String v) throws Exception {
		
		if (Strings.isNullOrBlank(v)) {
			return null;
		}
		
		try {
			ExternalSystemInterfaceBLManager esiBLmanager = new ExternalSystemInterfaceBLManager();
			ExternalSystemInterfaceInstanceData data = esiBLmanager.getExternalSystemInterfaceInstanceDataByName(v);
			return data.getEsiInstanceId();
		} catch (Exception e) {
			return RestValidationMessages.INVALID;
		}
	}

	@Override
	public String marshal(String v) throws Exception {
		if (v == null) {
			return "";
		}
		
		try {
			ExternalSystemInterfaceBLManager esiBLmanager = new ExternalSystemInterfaceBLManager();
			ExternalSystemInterfaceInstanceData data = esiBLmanager.getExternalSystemInterfaceInstanceDataById(v);
			return data.getName();
		} catch (Exception e) {
			return "";
		}
	}
}