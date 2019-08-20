package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

public class DiameterPeerGroupAdapter extends XmlAdapter<String, String>{

	@Override
	public String unmarshal(String value) throws Exception {
		
		if (Strings.isNullOrBlank(value)) {
			return null;
		}
		
		try {
			DiameterPeerGroupBLManager blManager = new DiameterPeerGroupBLManager();
			DiameterPeerGroup data = blManager.getDiameterPeerGroupByName(value.trim());
			return data.getPeerGroupId();
		} catch (Exception e) {
			return RestValidationMessages.INVALID;
		}
	}

	@Override
	public String marshal(String value) throws Exception {
		if (value == null) {
			return "";
		}
		
		try {
			DiameterPeerGroupBLManager blManager = new DiameterPeerGroupBLManager();
			DiameterPeerGroup data = blManager.getDiameterPeerGroupByName(value);
			return data.getPeerGroupName();
		} catch (Exception e) {
			return "";
		}
	}
}