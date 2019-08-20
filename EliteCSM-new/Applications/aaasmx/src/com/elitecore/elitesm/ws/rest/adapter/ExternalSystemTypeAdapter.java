package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;

/**
 * Parse ESI Type String to ESI Type Id.
 * if ESI Type not valid or null than we return -1L and give validation message.
 * @author chirag.i.prajapati
 */
public class ExternalSystemTypeAdapter extends XmlAdapter<String, Long>{

	private ExternalSystemInterfaceBLManager externalSystemBLManger = new ExternalSystemInterfaceBLManager(); 
	@Override
	public Long unmarshal(String esiType) {
		Long esiTypeId = null;
		try {
			if(Strings.isNullOrBlank(esiType) == false){
				esiTypeId = externalSystemBLManger.getEsiTypeIdFromName(esiType); 
			} else {
				esiTypeId = -1L;
			}
		}catch(Exception e){
			esiTypeId = -1L;
		}
		return esiTypeId;
	}

	@Override
	public String marshal(Long esiTypeId){
		String esiType = null;
		try {
			 esiType = externalSystemBLManger.getEsiTypeNameFromId(esiTypeId);
		}catch(Exception e){
			esiType = "";
		}
		return esiType;
	}
}
