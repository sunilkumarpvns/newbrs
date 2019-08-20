package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class TranslationMappingNameToIDAdapter extends XmlAdapter<String, String> {

	
	@Override
	public String unmarshal(String v) throws Exception {
		if (Strings.isNullOrBlank(v)) {
			return null;
		}
		
		try {
			TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
			TranslationMappingConfData data = translationMappingBLManager.getTranslationMappingConfDataByName(v);
			return data.getTranslationMapConfigId().toString();
		} catch (Exception e) {
			try {
				CopyPacketTransMapConfBLManager copyTransMapConfBLManager = new CopyPacketTransMapConfBLManager();
				CopyPacketTranslationConfData copyPacketData = copyTransMapConfBLManager.getCopyPacketTransMapConfigDetailDataByName(v);
				return copyPacketData.getCopyPacketTransConfId();
			} catch (Exception ex){
				return RestValidationMessages.INVALID;
			}
		}
	}

	@Override
	public String marshal(String v) throws Exception {
		if (v == null) {
			return "";
		}
		
		try {
			TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
			TranslationMappingConfData data = translationMappingBLManager.getTranslationMappingConfDataById(v);
			return data.getName();
		} catch (Exception e) {
			try {
				CopyPacketTransMapConfBLManager copyTransMapConfBLManager = new CopyPacketTransMapConfBLManager();
				CopyPacketTranslationConfData copyPacketData = copyTransMapConfBLManager.getCopyPacketTransMapConfigDetailDataById(v);
				return copyPacketData.getName();
			} catch (Exception ex){
				return "";
			}
		}
	}

}
