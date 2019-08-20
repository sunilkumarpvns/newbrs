package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;

public class ToTranslationAdapter extends XmlAdapter<String, String> {

	public static final String DIAMETER = "DIAMETER";
	public static final String CRESTEL_RATING="CRESTEL-RATING";
	public static final String RADIUS="RADIUS";
	public static final String CRESTEL_OCSv2="CRESTEL-OCSv2";

	@Override
	public String unmarshal(String toTranslationType) throws Exception {
		String toOrFromValue = null;

		if (DIAMETER.equalsIgnoreCase(toTranslationType)) {
			toOrFromValue = TranslationMappingConfigConstants.DIAMETER;
		} else if (CRESTEL_RATING.equalsIgnoreCase(toTranslationType)) {
			toOrFromValue = TranslationMappingConfigConstants.CRESTEL_RATING;
		} else if (RADIUS.equalsIgnoreCase(toTranslationType)) {
			toOrFromValue = TranslationMappingConfigConstants.RADIUS;
		} else if (CRESTEL_OCSv2.equalsIgnoreCase(toTranslationType)) {
			toOrFromValue = TranslationMappingConfigConstants.CRESTEL_OCSv2;
		} else {
			toOrFromValue = TranslationMappingConfigConstants.INVALID_VALUE;
		}
		return toOrFromValue;
	}

	@Override
	public String marshal(String toTranslationType) throws Exception {
		String toOrFromValue = null;
		if (TranslationMappingConfigConstants.DIAMETER.equals(toTranslationType)) {
			toOrFromValue = DIAMETER;
		} else if (TranslationMappingConfigConstants.CRESTEL_RATING.equals(toTranslationType)) {
			toOrFromValue = CRESTEL_RATING;
		} else if (TranslationMappingConfigConstants.RADIUS.equals(toTranslationType)) {
			toOrFromValue = RADIUS;
		} else if (TranslationMappingConfigConstants.CRESTEL_OCSv2.equals(toTranslationType)) {
			toOrFromValue = CRESTEL_OCSv2;
		} else {
			toOrFromValue = null;
		}
		return toOrFromValue;
	}

}
