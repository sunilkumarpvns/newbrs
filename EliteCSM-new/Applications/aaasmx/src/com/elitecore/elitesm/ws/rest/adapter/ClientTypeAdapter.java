package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;

/**
 * Client Type Adapter that returns Client Type based on Client Type id 
 * if Client Type not found than return -1l ,
 * And also returns Client Type id based on valid Client Type
 * @author chirag.i.prajapati
 *
 */
public class ClientTypeAdapter extends XmlAdapter<String, Long>{
	@Override
	public Long unmarshal(String clientType){
		ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
		
		Long clientTypeId = null;
		try {
			if(Strings.isNullOrBlank(clientType) == false){
				clientTypeId = clientProfileBLManager.getClientTypeIdFromName(clientType); 
			} else {
				clientTypeId = -1l;
			}
		}
		catch(Exception e){
			clientTypeId = -1l ;
		}
		return clientTypeId;
	}

	@Override
	public String marshal(Long clientTypeId) throws Exception {
		String clientType = null;
		try {
			clientType = EliteSMReferencialDAO.fetchClientTypeData(clientTypeId);
		} catch(Exception e){
			clientType = "";
		}
		return clientType;
	}
	
}
