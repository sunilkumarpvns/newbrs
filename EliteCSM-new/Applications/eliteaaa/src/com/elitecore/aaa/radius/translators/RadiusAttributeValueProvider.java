package com.elitecore.aaa.radius.translators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class RadiusAttributeValueProvider implements ValueProvider{
	private RadiusPacket packet;
	
	public RadiusAttributeValueProvider(RadiusPacket packet) {
		this.packet = packet;
	}
	
	@Override
	public String getStringValue(String identifier)
			throws InvalidTypeCastException,MissingIdentifierException {
		if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
			return String.valueOf(packet.getPacketType());
		}
		IRadiusAttribute attribute = packet.getRadiusAttribute(identifier,true);
		if(attribute != null){
			return getStringValue(attribute);
		}
		throw new MissingIdentifierException("Requested value not found in the request: " + identifier);
	}

	@Override
	public long getLongValue(String identifier)
			throws InvalidTypeCastException,MissingIdentifierException {
		if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
			return packet.getPacketType();
		}
		IRadiusAttribute attribute = packet.getRadiusAttribute(identifier,true);
		if(attribute != null)
			return attribute.getLongValue();
		else
			throw new MissingIdentifierException("Requested value not found in the request: "+identifier);
	
	}

	@Override
	public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		List<String> stringValues= null;
		if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
			stringValues=new ArrayList<String>(1);
			stringValues.add(String.valueOf(packet.getPacketType()));
			return stringValues;
		}
		Collection<IRadiusAttribute> iRadiusAttributeList = packet.getRadiusAttributes(identifier,true);
		if(iRadiusAttributeList!=null){
			stringValues=new ArrayList<String>();
			for(IRadiusAttribute iRadiusAttribute : iRadiusAttributeList){
				stringValues.add(getStringValue(iRadiusAttribute));
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
			longValues.add((long) packet.getPacketType());
			return longValues;
		}
		Collection<IRadiusAttribute> iRadiusAttributeList = packet.getRadiusAttributes(identifier,true);
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
	
	protected String getStringValue(IRadiusAttribute attribute) {
		return attribute.getStringValue();
	}

	protected byte[] getAuthenticator() {
		return this.packet.getAuthenticator();
	}
}
