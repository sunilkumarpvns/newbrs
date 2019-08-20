package com.elitecore.netvertex.gateway.radius;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class ValueProviderImpl implements ValueProvider{
	private RadServiceRequest request;
	private PCRFResponse response;

	public ValueProviderImpl(RadServiceRequest request) {
		this.request = request;
	}

	public ValueProviderImpl(PCRFResponse response) {
		this.response = response;
	}

	@Override
	public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
		String val = getValueInternal(identifier);
		if(val == null)
			throw new MissingIdentifierException("Configured identifier not found: " + identifier);
		try{
			return Long.parseLong(val);
		}catch(NumberFormatException nfe) {
			throw new InvalidTypeCastException("Invalid Numeric value found for Identifier '" + identifier + "' Value: " + val);
		}
	}

	@Override
	public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException{
		return getValueInternal(identifier);
	}

	private String getValueInternal(String identifier){
		IRadiusAttribute attribute;
		String strValue=null;
		if(request != null){
			if((attribute = request.getRadiusAttribute(true,identifier))!=null){
				strValue=attribute.getStringValue();
			}
		}else{
			strValue=response.getAttribute(identifier);
		}
		return strValue;
	}
	

	@Override
	public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		Collection<IRadiusAttribute> iRadiusAttributeList ;
		List<String> stringValues= null;	
		if(request != null){
			if((iRadiusAttributeList = request.getRadiusAttributes(true,identifier))!=null){
				stringValues=new ArrayList<String>();
				for(IRadiusAttribute iRadiusAttribute : iRadiusAttributeList){
					stringValues.add(iRadiusAttribute.getStringValue());
				}
			}else
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
		}else{
			String value=response.getAttribute(identifier);
			if(value!=null){
				stringValues=new ArrayList<String>(1);
				stringValues.add(value);
			}else
				throw new MissingIdentifierException("Configured identifier not found: " + identifier);
		}
		return stringValues;
	}

	@Override
	public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		Collection<IRadiusAttribute> iRadiusAttributeList ;
		List<Long> longValues;
		if(request != null){
			if((iRadiusAttributeList = request.getRadiusAttributes(true,identifier))!=null){
				longValues=new ArrayList<Long>();
				for(IRadiusAttribute iRadiusAttribute : iRadiusAttributeList){
					longValues.add(iRadiusAttribute.getLongValue());
				}
			}else
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
		}else{
			String value=response.getAttribute(identifier);
			if(value!=null){
				try{
					longValues=new ArrayList<Long>(1);
					longValues.add(Long.parseLong(value));
				}catch(NumberFormatException e){
					throw new InvalidTypeCastException(e.getMessage());
				}
			}else
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
		}
		return longValues;
	}

	@Override
	public Object getValue(String key) {
		return null;
	}
	
}