package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;

public class FromTranslationAdapter extends XmlAdapter<String, String> {

	public static final String DIAMETER = "DIAMETER";
	public static final String WEB_SERVICE="WEB-SERVICE";
	public static final String RADIUS="RADIUS";

	@Override
	public String unmarshal(String fromTranslationType) throws Exception {
		String toOrFromValue = null;

		if (DIAMETER.equalsIgnoreCase(fromTranslationType)) {
			toOrFromValue = TranslationMappingConfigConstants.DIAMETER;
		} else if (WEB_SERVICE.equalsIgnoreCase(fromTranslationType)) {
			toOrFromValue = TranslationMappingConfigConstants.WEB_SERVICE;
		} else if (RADIUS.equalsIgnoreCase(fromTranslationType)) {
			toOrFromValue = TranslationMappingConfigConstants.RADIUS;
		} else {
			toOrFromValue = TranslationMappingConfigConstants.INVALID_VALUE;
		}
		return toOrFromValue;
	}

	@Override
	public String marshal(String fromTranslationType) throws Exception {
		String toOrFromValue = null;
		if (TranslationMappingConfigConstants.DIAMETER.equals(fromTranslationType)) {
			toOrFromValue = DIAMETER;
		} else if (TranslationMappingConfigConstants.WEB_SERVICE.equals(fromTranslationType)) {
			toOrFromValue = WEB_SERVICE;
		} else if (TranslationMappingConfigConstants.RADIUS.equals(fromTranslationType)) {
			toOrFromValue = RADIUS;
		} else {
			toOrFromValue = null;
		}
		return toOrFromValue;
	}

}
