package com.elitecore.aaa.radius.session.imdg;

import javax.annotation.Nonnull;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

public class RadiusSessionDataValueProvider implements ValueProvider {

	private RadServiceRequest request;
	private RadServiceResponse response;

	public RadiusSessionDataValueProvider(@Nonnull RadServiceRequest request ,@Nonnull RadServiceResponse response) {
		this.request = request;
		this.response = response;
	}

	@Override
	public String getStringValue(String identifier) {
		IRadiusAttribute attribute= null;

		if (AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)) {
			return String.valueOf(request.getPacketType());
		} else if(identifier.startsWith("$REQ:")) {
			attribute = request.getRadiusAttribute(identifier.substring(5));
		} else if (identifier.startsWith("$RES:")) {
			attribute = response.getRadiusAttribute(identifier.substring(5));
		} else {
			attribute = request.getRadiusAttribute(identifier);
		}
		if(attribute != null) {
			return attribute.getStringValue();
		}
		return null;
	}

}
