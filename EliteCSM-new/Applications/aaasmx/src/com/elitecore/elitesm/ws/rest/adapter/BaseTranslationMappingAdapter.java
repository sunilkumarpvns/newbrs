package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
public class BaseTranslationMappingAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String translationMappingName) {

		String TranslationMappingId = null;
		if (Strings.isNullOrEmpty(translationMappingName) == false) {
			TranslationMappingConfBLManager transMappingBLManager = new TranslationMappingConfBLManager();

			try {
				TranslationMappingConfData data = null;
				data = transMappingBLManager.getTranslationMappingConfDataByName(translationMappingName);
				TranslationMappingId = data.getTranslationMapConfigId();
			} catch(Exception e) {
				TranslationMappingId = RestValidationMessages.INVALID ;
			}
		} 
		return TranslationMappingId;
	}

	@Override
	public String marshal(String translationMappingId) {

		String translationMappingName = null;
		TranslationMappingConfBLManager transMappingBLManager = new TranslationMappingConfBLManager();

		try {
			if (translationMappingId != null) {
				TranslationMappingConfData data = null;
				data = transMappingBLManager.getTranslationMappingConfDataById(translationMappingId);
				translationMappingName = data.getName();
			} 
		} catch(Exception exp) {
			exp.printStackTrace();
		}
		return translationMappingName;


	}

}
