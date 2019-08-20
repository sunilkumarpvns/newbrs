package com.elitecore.elitesm.ws.rest.adapter.eapconfig;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;

public class EAPCertificateTypeAdapter extends XmlAdapter<String, String>{

	@Override
	public String unmarshal(String certificateType) {
		String certificateTypeIds ="";
		try {
			if(Strings.isNullOrBlank(certificateType) == false){
				certificateTypeIds = EAPConfigUtils.convertEapCertificateTypeNameToTypeId(certificateType);
			}
		}catch(Exception e){
			certificateTypeIds ="";
		}
		return certificateTypeIds;
	}

	@Override
	public String marshal(String certificateTypeIds) throws Exception {
		String certificateTypeNames="";
		try {
			if(certificateTypeIds != null){
				certificateTypeNames = EAPConfigUtils.convertEapCertificateTypeIdToTypeName(certificateTypeIds);
			}
		} catch(Exception e){
			certificateTypeNames = "";
		}
		return certificateTypeNames;
	}
	
}
