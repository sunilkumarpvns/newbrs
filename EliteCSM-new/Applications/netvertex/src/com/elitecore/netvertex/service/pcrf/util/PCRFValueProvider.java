package com.elitecore.netvertex.service.pcrf.util;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.netvertex.core.bisummary.BICEASummary;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public class PCRFValueProvider implements ValueProvider {
	private PCRFRequest request;
	private PCRFResponse response;
	
	public PCRFValueProvider(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse) {
		this.request = pcrfRequest;
		this.response = pcrfResponse;
	}
	
	public PCRFValueProvider(PCRFRequest pcrfRequest) {
		this.request = pcrfRequest;
	}
	
	public PCRFValueProvider(PCRFResponse pcrfResponse) {
		this.response = pcrfResponse;
	}
	
	@Override
	public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
		String val = getValueInternal(identifier);
		if(val == null){
			throw new MissingIdentifierException("Configured identifier not found: " + identifier);
		}
		
		try{
			return Long.parseLong(val);
		}catch(NumberFormatException nfe){
			throw new InvalidTypeCastException("Invalid Numeric value found for Identifier '" + identifier + "'");
		}
	}
	
	@Override
	public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException{
		String val = getValueInternal(identifier);
		if(val == null){
			throw new MissingIdentifierException("Configured identifier not found: " + identifier);
		}
			
		return val;
	}

	private String getValueInternal(String identifier){
		
		if(request != null){
			String value = request.getAttribute(identifier);
			if(value != null){
				return value;
			}
		}
		
		if(response != null){
			String value = response.getAttribute(identifier);
			if(value != null){
				return value;
			}
		}
		
		
		return BICEASummary.getInstance().getValue(identifier);
	}


	@Override
	public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		String value = getValueInternal(identifier);
		if(value==null){
			throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			
		}
		
		List<String> stringValues=new ArrayList<String>(1);	
		stringValues.add(value);
		return stringValues;
	}

	@Override
	public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		String value = getValueInternal(identifier);
		if(value == null){
			throw new MissingIdentifierException("Configured identifier not found: "+identifier);
		}
		
		try{
			List<Long> longValues=new ArrayList<Long>(1);
			longValues.add(Long.parseLong(value));
			return longValues;
		}catch(Exception e){
			throw new InvalidTypeCastException(e.getMessage());
		}
			
	}
	
	
	public PCRFResponse getPCRFResponse(){
		return response;
	}
	
	public PCRFRequest getPCRFRequest(){
		return request;
	}

	@Override
	public Object getValue(String key) {
		return null;
	}
}
