package com.elitecore.aaa.radius.util.exprlib;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

/**
 * Provides the radius specific syntax for expression library. It supports accessing
 * the values from request packet as well as response packet using the prefixes
 * {@code REQ:} and {@code RES:} respectively.
 * <br/><br/>
 * If none of the prefixes are present then by default <b>request</b> packet is used to find 
 * the identifier provided.
 *  
 * <br/><br/>
 * Example: <code>REQ:0:1 = "some value" or RES:0:27 = "100"</code>
 * 
 * @author narendra.pathai
 *
 */
public class DefaultValueProvider extends ValueProviderSupport {
	private final RadServiceRequest request;
	private final RadServiceResponse response;

	public DefaultValueProvider(@Nonnull RadServiceRequest request,
			@Nonnull RadServiceResponse response) {
		this.request = request;
		this.response = response;
	}

	protected IRadiusAttribute getAttributeFromResponse(String attributeId) {
		return response.getRadiusAttribute(true, attributeId);
	}

	protected IRadiusAttribute getAttributeFromRequest(String attributeId) {
		return request.getRadiusAttribute(attributeId, true);
	}

	protected Collection<IRadiusAttribute> getAttributesFromResponse(
			String attributeId) {
		return response.getRadiusAttributes(true, attributeId);
	}

	protected Collection<IRadiusAttribute> getAttributesFromRequest(
			String attributeId) {
		return request.getRadiusAttributes(attributeId, true);
	}

	@Override
	public Object getValue(String key) {
		return null;
	}
}
