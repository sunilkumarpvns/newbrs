package com.elitecore.diameterapi.diameter.common.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.core.commons.exprlib.AttributeValueProvider;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.MappedValueProvider;

public class DiameterAVPValueProvider extends MappedValueProvider implements AttributeValueProvider {

	private @Nonnull final DiameterPacket diameterPacket;
	
	public DiameterAVPValueProvider(@Nonnull DiameterPacket diameterPacket) {
		super(diameterPacket.getParameters());
		this.diameterPacket = diameterPacket;
	}
	
	@Override
	public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
		return getStringValue(identifier, USE_BASE_VALUE);
	}

	@Override
	public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
		IDiameterAVP diameterAvp = diameterPacket.getAVP(identifier, true);
		if (diameterAvp != null) {
			return diameterAvp.getInteger();
		}
		if (TranslatorConstants.COMMAND_CODE.equalsIgnoreCase(identifier)) {
			return diameterPacket.getCommandCode();
		}
		if (TranslatorConstants.APPLICATION_ID.equalsIgnoreCase(identifier)) {
			return diameterPacket.getApplicationID();
		}
		if (TranslatorConstants.ERROR_FLAG.equalsIgnoreCase(identifier)) {
			return DiameterUtility.booleanToInt(diameterPacket.isError());
		}
		if (TranslatorConstants.RETRANSMITTED_FLAG.equalsIgnoreCase(identifier)) {
			return DiameterUtility.booleanToInt(diameterPacket.isReTransmitted());
		}
		if (TranslatorConstants.PROXY_FLAG.equalsIgnoreCase(identifier)) {
			return DiameterUtility.booleanToInt(diameterPacket.isProxiable());
		}
		throw new MissingIdentifierException("Configured identifier not found: "+identifier);
	}

	@Override
	public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		List<IDiameterAVP> diameterAVPList=diameterPacket.getAVPList(identifier,true);
		if(diameterAVPList!=null){
			List<String> stringValues=new ArrayList<String>();
			for(IDiameterAVP iDiameterAVP : diameterAVPList){
				stringValues.add(iDiameterAVP.getStringValue());
			}
			return stringValues;
		}
		if (TranslatorConstants.COMMAND_CODE.equalsIgnoreCase(identifier)) {
			List<String> stringValues = new ArrayList<String>(2);	
			stringValues.add(String.valueOf(diameterPacket.getCommandCode()));
			return stringValues;
		}
		if (TranslatorConstants.APPLICATION_ID.equalsIgnoreCase(identifier)) {
			List<String> stringValues = new ArrayList<String>(2);	
			stringValues.add(String.valueOf(diameterPacket.getApplicationID()));
			return stringValues;
		}
		if (TranslatorConstants.ERROR_FLAG.equalsIgnoreCase(identifier)) {
			List<String> stringValues = new ArrayList<String>(2);	
			stringValues.add(String.valueOf(DiameterUtility.booleanToInt(diameterPacket.isError())));
			return stringValues;
		}
		if (TranslatorConstants.PROXY_FLAG.equalsIgnoreCase(identifier)) {
			List<String> stringValues = new ArrayList<String>(2);	
			stringValues.add(String.valueOf(DiameterUtility.booleanToInt(diameterPacket.isProxiable())));
			return stringValues;
		}
		if (TranslatorConstants.RETRANSMITTED_FLAG.equalsIgnoreCase(identifier)) {
			List<String> stringValues = new ArrayList<String>(2);	
			stringValues.add(String.valueOf(DiameterUtility.booleanToInt(diameterPacket.isReTransmitted())));
			return stringValues;
		}
		throw new MissingIdentifierException("Configured identifier not found: "+identifier);
	}

	@Override
	public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		List<IDiameterAVP> diameterAVPList=diameterPacket.getAVPList(identifier,true);
		if(diameterAVPList!=null){
			List<Long> longValues=new ArrayList<Long>();
			for(IDiameterAVP iDiameterAVP : diameterAVPList){
				longValues.add(iDiameterAVP.getInteger());
			}
			return longValues;
		}
		if (TranslatorConstants.COMMAND_CODE.equalsIgnoreCase(identifier)) {
			List<Long> longValues = new ArrayList<Long>(2);	
			longValues.add((long)diameterPacket.getCommandCode());
			return longValues;
		}
		if (TranslatorConstants.APPLICATION_ID.equalsIgnoreCase(identifier)) {
			List<Long> longValues = new ArrayList<Long>(2);	
			longValues.add(diameterPacket.getApplicationID());
			return longValues;
		}
		if (TranslatorConstants.ERROR_FLAG.equalsIgnoreCase(identifier)) {
			List<Long> longValues = new ArrayList<Long>(2);	
			longValues.add((long) DiameterUtility.booleanToInt(diameterPacket.isError()));
			return longValues;
		}
		if (TranslatorConstants.PROXY_FLAG.equalsIgnoreCase(identifier)) {
			List<Long> longValues = new ArrayList<Long>(2);	
			longValues.add((long) DiameterUtility.booleanToInt(diameterPacket.isProxiable()));
			return longValues;
		}
		if (TranslatorConstants.RETRANSMITTED_FLAG.equalsIgnoreCase(identifier)) {
			List<Long> longValues = new ArrayList<Long>(2);	
			longValues.add((long) DiameterUtility.booleanToInt(diameterPacket.isReTransmitted()));
			return longValues;
		}
		throw new MissingIdentifierException("Configured identifier not found: "+identifier);
	}

	@Override
	public String getDictionaryKey(String identifier) throws MissingIdentifierException, InvalidTypeCastException {
		return getStringValue(identifier, USE_BASE_VALUE);
	}

	private String getStringValue(String identifier, boolean useDictionaryValue)
			throws MissingIdentifierException {
		
		IDiameterAVP diameterAvp = diameterPacket.getAVP(identifier, true);
		if (diameterAvp != null) {
			return diameterAvp.getStringValue(useDictionaryValue);
		}
		if (TranslatorConstants.COMMAND_CODE.equalsIgnoreCase(identifier)) {
			return String.valueOf(diameterPacket.getCommandCode());
		}
		if (TranslatorConstants.APPLICATION_ID.equalsIgnoreCase(identifier)) {
			return String.valueOf(diameterPacket.getApplicationID());
		}
		if (TranslatorConstants.ERROR_FLAG.equalsIgnoreCase(identifier)) {
			return String.valueOf(DiameterUtility.booleanToInt(diameterPacket.isError()));
		}
		if (TranslatorConstants.RETRANSMITTED_FLAG.equalsIgnoreCase(identifier)) {
			return String.valueOf(DiameterUtility.booleanToInt(diameterPacket.isReTransmitted()));
		}
		if (TranslatorConstants.PROXY_FLAG.equalsIgnoreCase(identifier)) {
			return String.valueOf(DiameterUtility.booleanToInt(diameterPacket.isProxiable()));
		}
		throw new MissingIdentifierException("Configured identifier not found: " + identifier);
		
	}
}
