package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.util.constants.RestValidationMessages;
public class RollingTypeAdapter extends XmlAdapter<String, Long> {

	private static final long SIZE_BASED_VALUE = 2l;
	private static final long TIME_BASED_VALUE = 1l;

	@Override
	public Long unmarshal(String rollingType) {

		Long rollingTypevalue = null;
		if (RestValidationMessages.TIME_BASED.equals(rollingType)) {
			rollingTypevalue = TIME_BASED_VALUE;
		} else if (RestValidationMessages.SIZE_BASED.equals(rollingType)) {
			rollingTypevalue = SIZE_BASED_VALUE;
		} else {
			rollingTypevalue = -1l;
		}
		
		return rollingTypevalue;
	}

	@Override
	public String marshal(Long translationMappingId) {

		String rollingTypevalue = null;
		
		if (TIME_BASED_VALUE == translationMappingId) {
			rollingTypevalue = RestValidationMessages.TIME_BASED;
		} else if (SIZE_BASED_VALUE == translationMappingId) {
			rollingTypevalue = RestValidationMessages.SIZE_BASED;
		} else {
			rollingTypevalue = RestValidationMessages.INVALID_VALUE;
		}
		
		return rollingTypevalue;

	}
}
