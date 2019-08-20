package com.elitecore.aaa.radius.util.exprlib;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

/**
 * Provides the radius specific syntax for expression library. It supports accessing
 * the values only from request packet prefix {@code REQ:}
 * <br/><br/>
 * If prefix is not present then by default <b>request</b> packet is used to find 
 * the identifier provided.
 *  
 * <br/><br/>
 * Example: <code>REQ:0:1 = "some value" or 0:1 = "*"</code>
 * 
 * @author narendra.pathai
 *
 */
public class RequestOnlyValueProvider extends ValueProviderSupport {

	private final RadServiceRequest request;

	public RequestOnlyValueProvider(@Nonnull RadServiceRequest request) {
		this.request = request;
	}
	
	@Override
	protected IRadiusAttribute getAttributeFromResponse(String attributeId) {
		return null; //always return null
	}

	@Override
	protected IRadiusAttribute getAttributeFromRequest(String attributeId) {
		return request.getRadiusAttribute(attributeId, true);
	}

	@Override
	protected Collection<IRadiusAttribute> getAttributesFromResponse(
			String attributeId) {
		return Collections.emptyList(); //always return empty list
	}

	@Override
	protected Collection<IRadiusAttribute> getAttributesFromRequest(
			String attributeId) {
		return request.getRadiusAttributes(attributeId, true);
	}

	@Override
	public Object getValue(String key) {
		return null;
	}

}
