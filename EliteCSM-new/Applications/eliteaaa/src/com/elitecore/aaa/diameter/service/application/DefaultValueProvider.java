package com.elitecore.aaa.diameter.service.application;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.MappedValueProvider;

/**
 * 
 * @author narendra.pathai
 *
 */

// FIXME Code for command code, error flag in case of multiple value expression. getLongValues and getStringValues
public class DefaultValueProvider extends MappedValueProvider {

	private static final String requestMappingRegx = "REQ:[0-9]+[:[0-9]+]+";
	private static final String responseMappingRegx = "RES:[0-9]+[:[0-9]+]+";
	private static final Pattern requestMappingRegex;
	private static final Pattern responseMappingRegex;
	private ApplicationRequest request;
	private ApplicationResponse response;

	static {
		requestMappingRegex = Pattern.compile(requestMappingRegx);
		responseMappingRegex = Pattern.compile(responseMappingRegx);
	}
	
	public DefaultValueProvider(ApplicationRequest request, ApplicationResponse response) {
		super(request.getDiameterRequest().getParameters());
		this.request = request;
		this.response = response;
	}
	
	@Override
	public String getStringValue(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		
		if (TranslatorConstants.COMMAND_CODE.equalsIgnoreCase(identifier)) {
			return String.valueOf(request.getDiameterRequest().getCommandCode());
		}
		if (TranslatorConstants.APPLICATION_ID.equalsIgnoreCase(identifier)) {
			return String.valueOf(request.getDiameterRequest().getApplicationID());
		}
		if (TranslatorConstants.ERROR_FLAG.equalsIgnoreCase(identifier)) {
			return String.valueOf(DiameterUtility.booleanToInt(request.getDiameterRequest().isError()));
		}
		if (TranslatorConstants.RETRANSMITTED_FLAG.equalsIgnoreCase(identifier)) {
			return String.valueOf(DiameterUtility.booleanToInt(request.getDiameterRequest().isReTransmitted()));
		}
		if (TranslatorConstants.PROXY_FLAG.equalsIgnoreCase(identifier)) {
			return String.valueOf(DiameterUtility.booleanToInt(request.getDiameterRequest().isProxiable()));
		}
		
		IDiameterAVP avp;
		if (requestMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			avp = getAttributeFromRequest(attributeId);
		} else if (responseMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			avp = getAttributeFromResponse(attributeId);
		} else {
			avp = getAttributeFromRequest(identifier);
			
		}
		
		if (avp == null) {
			throw new MissingIdentifierException("Configured identifier: " 
					+ identifier + " not found");
		}
		
		return avp.getStringValue();
	}

	@Override
	public long getLongValue(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		if (TranslatorConstants.COMMAND_CODE.equalsIgnoreCase(identifier)) {
			return request.getDiameterRequest().getCommandCode();
		}
		if (TranslatorConstants.APPLICATION_ID.equalsIgnoreCase(identifier)) {
			return request.getDiameterRequest().getApplicationID();
		}
		if (TranslatorConstants.ERROR_FLAG.equalsIgnoreCase(identifier)) {
			return DiameterUtility.booleanToInt(request.getDiameterRequest().isError());
		}
		if (TranslatorConstants.RETRANSMITTED_FLAG.equalsIgnoreCase(identifier)) {
			return DiameterUtility.booleanToInt(request.getDiameterRequest().isReTransmitted());
		}
		if (TranslatorConstants.PROXY_FLAG.equalsIgnoreCase(identifier)) {
			return DiameterUtility.booleanToInt(request.getDiameterRequest().isProxiable());
		}
		
		IDiameterAVP avp;
		if (requestMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			avp = getAttributeFromRequest(attributeId);
		} else if (responseMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			avp = getAttributeFromResponse(attributeId);
		} else {
			avp = getAttributeFromRequest(identifier);
			
		}
		
		if (avp == null) {
			throw new MissingIdentifierException("Configured identifier: " 
					+ identifier + " not found");
		}
		
		return avp.getInteger();
	}

	@Override
	public List<String> getStringValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		Collection<IDiameterAVP> attributes = getAttributes(identifier);
		return Collectionz.map(attributes, new Function<IDiameterAVP, String>() {
			public String apply(IDiameterAVP input) {
				return input.getStringValue();
			};
		});
	}

	@Override
	public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		Collection<IDiameterAVP> attributes = getAttributes(identifier);
		return Collectionz.map(attributes, new Function<IDiameterAVP, Long>() {
			public Long apply(IDiameterAVP input) {
				return input.getInteger();
			};
		});
	}

	
	private String attributeId(String token) {
		// it is safe to take index + 1, as the pattern is already validated
		return token.substring(token.indexOf(':') + 1);
	}

	private IDiameterAVP getAttributeFromResponse(String attributeId) {
		return response.getAVP(attributeId, true);
	}

	private IDiameterAVP getAttributeFromRequest(String attributeId) {
		return request.getAVP(attributeId, true);
	}

	private Collection<IDiameterAVP> getAttributes(String identifier) 
	throws MissingIdentifierException {
		Collection<IDiameterAVP> avps;
		if (requestMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			avps = getAttributesFromRequest(attributeId);
		} else if (responseMappingRegex.matcher(identifier).matches()) {
			String attributeId = attributeId(identifier);
			avps = getAttributesFromResponse(attributeId);
		} else {
			avps = getAttributesFromRequest(identifier);
		}
		if (avps == null) {
			throw new MissingIdentifierException("Configured identifier: " 
					+ identifier + " not found");
		}
		return avps;
	}

	private Collection<IDiameterAVP> getAttributesFromResponse(String attributeId) {
		return response.getDiameterAnswer().getAVPList(attributeId, true);
	}

	private Collection<IDiameterAVP> getAttributesFromRequest(String attributeId) {
		return request.getDiameterRequest().getAVPList(attributeId, true);
	}

}
