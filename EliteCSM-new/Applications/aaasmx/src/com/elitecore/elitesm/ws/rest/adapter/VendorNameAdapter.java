package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.clientprofile.ClientProfileBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;

/**
 * Vendor Name Adapter that returns Vendor Name based on Vendor instance id , 
 * if Vendor Name not found it will return -1l, 
 * And also returns Vendor instance id based on Vendor Name
 * @author chirag.i.prajapati
 *
 */
public class VendorNameAdapter extends XmlAdapter<String, String>{
	
	@Override
	public String unmarshal(String vendorName) throws Exception {
		ClientProfileBLManager clientProfileBLManager = new ClientProfileBLManager();
		
		String vendorId = null;
		try {
			if(Strings.isNullOrBlank(vendorName) == false){
				vendorId = clientProfileBLManager.getVendorIdFromName(vendorName); 
			} else {
				vendorId = RestValidationMessages.INVALID;
			}
		}
		catch(Exception e){
			vendorId =  RestValidationMessages.INVALID; ;
		}
		return vendorId;
	}

	@Override
	public String marshal(String vendorId) throws Exception {
		String vendorName = null;
		try {
			vendorName = EliteSMReferencialDAO.fetchVendorData(vendorId);
		} catch(Exception e){
			vendorName = "";
		}
		return vendorName;
	}

}
