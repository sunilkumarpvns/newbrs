package com.elitecore.aaa.radius.translators.keyword;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

public class RequestValueProvider implements ValueProvider {
	private RadServiceRequest request;

	public RequestValueProvider(RadServiceRequest request) {
		this.request = request;
	}
	
	@Override
	public String getStringValue(String identifier) {
		IRadiusAttribute attribute = request.getRadiusAttribute(identifier, true);
		if (attribute == null) {
			return null;
		}
		
		return attribute.getStringValue();
	}
	
}
