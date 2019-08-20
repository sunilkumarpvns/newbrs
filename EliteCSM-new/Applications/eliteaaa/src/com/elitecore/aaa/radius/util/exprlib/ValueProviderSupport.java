package com.elitecore.aaa.radius.util.exprlib;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * Provides basic syntax verification and packet decision mechanism and leaves it 
 * upto the concrete classes to provide with the attribute value. Supports prefixes
 * {@code REQ:} and {@code RES:}. If none of the prefix is present then request packet
 * is used to find the identifier provided.
 * 
 * @author narendra.pathai
 *
 */
public abstract class ValueProviderSupport implements ValueProvider {
	private static final String requestMappingRegx = "REQ:[0-9]+[:[0-9]+]+";
	private static final String responseMappingRegx = "RES:[0-9]+[:[0-9]+]+";
	private static final Pattern requestMappingRegex;
	private static final Pattern responseMappingRegex;

	static {
		requestMappingRegex = Pattern.compile(requestMappingRegx);
		responseMappingRegex = Pattern.compile(responseMappingRegx);
	}

	@Override
	public String getStringValue(String identifier)
	throws InvalidTypeCastException, MissingIdentifierException {
		IRadiusAttribute attribute = getAttribute(identifier);
		return attribute.getStringValue(true);
	}

	@Override
	public long getLongValue(String identifier)
	throws InvalidTypeCastException, MissingIdentifierException {
		IRadiusAttribute attribute = getAttribute(identifier);
		return attribute.getLongValue();
	}

	@Override
	public List<String> getStringValues(String identifier)
	throws InvalidTypeCastException, MissingIdentifierException {
		Collection<IRadiusAttribute> attributes = getAttributes(identifier);
		return Collectionz.map(attributes, new Function<IRadiusAttribute, String>() {
			public String apply(IRadiusAttribute input) {
				return input.getStringValue(true);
			};
		});
	}

	@Override
	public List<Long> getLongValues(String identifier)
	throws InvalidTypeCastException, MissingIdentifierException {
		Collection<IRadiusAttribute> attributes = getAttributes(identifier);
		return Collectionz.map(attributes, new Function<IRadiusAttribute, Long>() {
			public Long apply(IRadiusAttribute input) {
				return input.getLongValue();
			};
		});
	}

	private String attributeId(String token) {
		// it is safe to take index + 1, as the pattern is already validated
		return token.substring(token.indexOf(':') + 1);
	}

	private IRadiusAttribute getAttribute(String identifier) 
	throws MissingIdentifierException {
		IRadiusAttribute radiusAttribute;
		if (requestMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			radiusAttribute = getAttributeFromRequest(attributeId);
		} else if (responseMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			radiusAttribute = getAttributeFromResponse(attributeId);
		} else {
			radiusAttribute = getAttributeFromRequest(identifier);
		}
		if (radiusAttribute == null) {
			throw new MissingIdentifierException("Configured identifier: " 
					+ identifier + " not found");
		}
		return radiusAttribute;
	}

	private Collection<IRadiusAttribute> getAttributes(String identifier) 
	throws MissingIdentifierException {
		Collection<IRadiusAttribute> radiusAttributes;
		if (requestMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			radiusAttributes = getAttributesFromRequest(attributeId);
		} else if (responseMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			radiusAttributes = getAttributesFromResponse(attributeId);
		} else {
			radiusAttributes = getAttributesFromRequest(identifier);
		}
		if (radiusAttributes == null) {
			throw new MissingIdentifierException("Configured identifier: " 
					+ identifier + " not found");
		}
		return radiusAttributes;
	}

	/**
	 * Lookup the attribute with {@code attributeId} from response, null if attribute
	 * is not found or operation is not supported
	 * 
	 * @param attributeId id of the attribute to be found, example 0:1
	 * @return attribute from response, null if attribute is not found or operation is 
	 * not supported
	 */
	protected abstract @Nullable IRadiusAttribute getAttributeFromResponse(String attributeId);	
	/**
	 * Lookup the attribute with {@code attributeId} from request, null if attribute
	 * is not found or operation is not supported
	 * 
	 * @param attributeId id of the attribute to be found, example 0:1
	 * @return attribute from request, null if attribute is not found or operation is 
	 * not supported
	 */
	protected abstract @Nullable IRadiusAttribute getAttributeFromRequest(String attributeId);
	/**
	 * Lookup the attributes with {@code attributeId} from response, null or empty list
	 * if attribute is not found or operation is not supported
	 * 
	 * @param attributeId id of the attribute to be found, example 0:1
	 * @return attributes from response, null or empty list if attribute is not found or operation is 
	 * not supported
	 */
	protected abstract @Nullable Collection<IRadiusAttribute> getAttributesFromResponse(
			String attributeId);
	/**
	 * Lookup the attributes with {@code attributeId} from request, null or empty list
	 * if attribute is not found or operation is not supported
	 * 
	 * @param attributeId id of the attribute to be found, example 0:1
	 * @return attributes from request, null or empty list if attribute is not found or operation is 
	 * not supported
	 */
	protected abstract @Nullable Collection<IRadiusAttribute> getAttributesFromRequest(
			String attributeId);
}
