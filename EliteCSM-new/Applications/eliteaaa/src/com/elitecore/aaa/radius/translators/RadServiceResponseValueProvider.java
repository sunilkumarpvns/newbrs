package com.elitecore.aaa.radius.translators;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.core.commons.exprlib.AttributeValueProvider;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;

public class RadServiceResponseValueProvider implements AttributeValueProvider {
	
	private @Nonnull RadServiceResponse response;

	public RadServiceResponseValueProvider(@Nonnull RadServiceResponse response) {
		this.response = checkNotNull(response, "Service response is null");
	}

	@Override
	public String getStringValue(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		return getStringValue(identifier, USE_DICTIONARY_VALUE);
	}

	@Override
	public long getLongValue(String identifier)
			throws InvalidTypeCastException,MissingIdentifierException {
		IRadiusAttribute attribute = response.getRadiusAttribute(true, identifier);
		if (attribute != null) {
			return attribute.getLongValue();
		}
		throw new MissingIdentifierException("Requested value for " + identifier + " not found in packet");

	}

	@Override
	public List<String> getStringValues(String identifier) 
			throws InvalidTypeCastException, MissingIdentifierException {
		List<String> stringValues= null;
		Collection<IRadiusAttribute> attributeList = response.getRadiusAttributes(true, identifier);
		if (attributeList != null) {
			stringValues = Collectionz.map(attributeList, new Function<IRadiusAttribute, String>() {

				@Override
				public String apply(IRadiusAttribute attribute) {
					return attribute.getStringValue(true);
				}
				
			});
		} else {
			throw new MissingIdentifierException("Requested value for " + identifier + " not found in packet");
		}
		return stringValues;
	}

	@Override
	public List<Long> getLongValues(String identifier) 
			throws InvalidTypeCastException, MissingIdentifierException {
		List<Long> longValues = null;
		Collection<IRadiusAttribute> attributeList = response.getRadiusAttributes(true, identifier);
		if (attributeList != null) {
			longValues = Collectionz.map(attributeList, new Function<IRadiusAttribute, Long>() {

				@Override
				public Long apply(IRadiusAttribute attribute) {
					return attribute.getLongValue();
				}
				
			});
		} else {
			throw new MissingIdentifierException("Requested value for " + identifier + " not found in packet");
		}
		return longValues;
	}

	@Override
	public Object getValue(String key) {
		return null;
	}

	@Override
	public String getDictionaryKey(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		return getStringValue(identifier, USE_BASE_VALUE);
	}

	private String getStringValue(String identifier, boolean useDictionaryValue) throws MissingIdentifierException {
		IRadiusAttribute attribute = response.getRadiusAttribute(true, identifier);
		if (attribute != null) {
			return attribute.getStringValue(useDictionaryValue);
		}
		
		throw new MissingIdentifierException("Requested value for " + identifier + " not found in response");
	}
}