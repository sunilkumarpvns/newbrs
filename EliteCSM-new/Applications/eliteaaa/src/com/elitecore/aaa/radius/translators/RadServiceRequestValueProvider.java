package com.elitecore.aaa.radius.translators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.core.commons.exprlib.AttributeValueProvider;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;

public class RadServiceRequestValueProvider implements AttributeValueProvider {
	private @Nonnull final RadServiceRequest request;

	public RadServiceRequestValueProvider(@Nonnull RadServiceRequest request) {
		this.request = request;
	}

	@Override
	public String getStringValue(String identifier)
			throws InvalidTypeCastException,MissingIdentifierException {
		return getStringValue(identifier, USE_DICTIONARY_VALUE);
	}

	@Override
	public long getLongValue(String identifier)
			throws InvalidTypeCastException,MissingIdentifierException {
		if (AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)) {
			return request.getPacketType();
		}
		
		IRadiusAttribute attribute = request.getRadiusAttribute(identifier, true);
		if (attribute != null) {
			return attribute.getLongValue();
		} 
			
		throw new MissingIdentifierException("Requested value not found in the request: " + identifier);
	}

	@Override
	public List<String> getStringValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		List<String> stringValues= null;
		if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
			stringValues=new ArrayList<String>(1);
			stringValues.add(String.valueOf(request.getPacketType()));
			return stringValues;
		}
		Collection<IRadiusAttribute> iRadiusAttributeList = request.getRadiusAttributes(identifier,true);
		if(iRadiusAttributeList!=null){
			stringValues=new ArrayList<String>();
			for(IRadiusAttribute iRadiusAttribute : iRadiusAttributeList){
				stringValues.add(iRadiusAttribute.getStringValue());
			}
		}else{
			throw new MissingIdentifierException("Requested value not found in the request: " + identifier);
		}
		return stringValues;
	}

	@Override
	public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		List<Long> longValues= null;
		if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
			longValues=new ArrayList<Long>(1);
			longValues.add((long) request.getPacketType());
			return longValues;
		}
		Collection<IRadiusAttribute> iRadiusAttributeList = request.getRadiusAttributes(identifier,true);
		if(iRadiusAttributeList!=null){
			longValues=new ArrayList<Long>();
			for(IRadiusAttribute iRadiusAttribute : iRadiusAttributeList){
				longValues.add(iRadiusAttribute.getLongValue());
			}
		}else{
			throw new MissingIdentifierException("Requested value not found in the request: " + identifier);
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

	private String getStringValue(String identifier, boolean useDictionaryValue) throws MissingIdentifierException{
		if (AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)) {
			return String.valueOf(request.getPacketType());
		}
		
		IRadiusAttribute attribute = request.getRadiusAttribute(identifier, true);
		if (attribute != null) {
			return attribute.getStringValue(useDictionaryValue);
		}
		
		throw new MissingIdentifierException("Requested value for " + identifier + " not found in request");
	}

}
