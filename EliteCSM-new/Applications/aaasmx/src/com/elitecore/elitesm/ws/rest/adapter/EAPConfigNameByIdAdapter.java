package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class EAPConfigNameByIdAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String v) throws Exception {
		
		try {
			if (Strings.isNullOrBlank(v)) {
				return null;
			}

			EAPConfigBLManager configBLManager = new EAPConfigBLManager();
			EAPConfigData data = configBLManager.getEapConfigurationDataByName(v);
			return data.getEapId();
		} catch (Exception e) {
			return RestValidationMessages.INVALID;
		}
	}

	@Override
	public String marshal(String v) throws Exception {
		if (v == null) {
			return null;
		}
		
		EAPConfigBLManager configBLManager = new EAPConfigBLManager();
		EAPConfigData data = configBLManager.getEapConfigurationDataById(v);
		return data.getName();
	}
}
