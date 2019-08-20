package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;

public class DiameterConcurrecyNameByIDAdapter extends XmlAdapter<String, String>{

	@Override
	public String unmarshal(String v) throws Exception {
		
		if (Strings.isNullOrBlank(v)) {
			return null;
		}
		
		
		try {
			DiameterConcurrencyBLManager concurrencyBLmanager = new DiameterConcurrencyBLManager();
			DiameterConcurrencyData data = concurrencyBLmanager.getDiameterConcurrencyDataByName(v);
			return data.getDiaConConfigId();
		} catch (Exception e) {
			e.printStackTrace();
			return "-1L";
		}
	}

	@Override
	public String marshal(String v) throws Exception {
		
		if (v == null) {
			return  "";
		}
		
		try {
			DiameterConcurrencyBLManager concurrencyBLmanager = new DiameterConcurrencyBLManager();
			DiameterConcurrencyData data = concurrencyBLmanager.getDiameterConcurrencyDataById(v);
			return data.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
