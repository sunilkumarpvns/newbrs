package com.elitecore.aaa.radius.util.exprlib;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * Provides the diameter specific syntax for expression library. It supports accessing
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
public class DefaultDiameterValueProvider implements ValueProvider {

	private static final String requestMappingRegx = "REQ:[0-9]+[:[0-9]+]+";
	private static final String responseMappingRegx = "RES:[0-9]+[:[0-9]+]+";
	private static final Pattern requestMappingRegex;
	private static final Pattern responseMappingRegex;
	private final DiameterRequest request;
	private final DiameterAnswer answer;

	static {
		requestMappingRegex = Pattern.compile(requestMappingRegx);
		responseMappingRegex = Pattern.compile(responseMappingRegx);
	}
	
	public DefaultDiameterValueProvider(@Nonnull DiameterRequest request, @Nonnull DiameterAnswer answer) {
		this.request = request;
		this.answer = answer;
	}

	@Override
	public String getStringValue(String identifier)
	throws InvalidTypeCastException, MissingIdentifierException {
		IDiameterAVP attribute = getAttribute(identifier);
		return attribute.getStringValue(true);
	}

	@Override
	public long getLongValue(String identifier)
	throws InvalidTypeCastException, MissingIdentifierException {
		IDiameterAVP attribute = getAttribute(identifier);
		return attribute.getInteger();
	}

	@Override
	public List<String> getStringValues(String identifier)
	throws InvalidTypeCastException, MissingIdentifierException {
		Collection<IDiameterAVP> attributes = getAttributes(identifier);
		return Collectionz.map(attributes, new Function<IDiameterAVP, String>() {
			public String apply(IDiameterAVP input) {
				return input.getStringValue(true);
			};
		});
	}

	@Override
	public List<Long> getLongValues(String identifier)
	throws InvalidTypeCastException, MissingIdentifierException {
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

	private IDiameterAVP getAttribute(String identifier) 
	throws MissingIdentifierException {
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
		return avp;
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

	/**
	 * Lookup the attribute with {@code attributeId} from response, null if attribute
	 * is not found or operation is not supported
	 * 
	 * @param attributeId id of the attribute to be found, example 0:1
	 * @return attribute from response, null if attribute is not found or operation is 
	 * not supported
	 */
	private IDiameterAVP getAttributeFromResponse(String attributeId) {
		return answer.getAVP(attributeId, true);
	}

	/**
	 * Lookup the attribute with {@code attributeId} from request, null if attribute
	 * is not found or operation is not supported
	 * 
	 * @param attributeId id of the attribute to be found, example 0:1
	 * @return attribute from request, null if attribute is not found or operation is 
	 * not supported
	 */
	private IDiameterAVP getAttributeFromRequest(String attributeId) {
		return request.getAVP(attributeId, true);
	}

	/**
	 * Lookup the attributes with {@code attributeId} from response, null or empty list
	 * if attribute is not found or operation is not supported
	 * 
	 * @param attributeId id of the attribute to be found, example 0:1
	 * @return attributes from response, null or empty list if attribute is not found or operation is 
	 * not supported
	 */
	private Collection<IDiameterAVP> getAttributesFromResponse(
			String attributeId) {
		return answer.getAVPList(attributeId, true);
	}

	/**
	 * Lookup the attributes with {@code attributeId} from request, null or empty list
	 * if attribute is not found or operation is not supported
	 * 
	 * @param attributeId id of the attribute to be found, example 0:1
	 * @return attributes from request, null or empty list if attribute is not found or operation is 
	 * not supported
	 */
	private Collection<IDiameterAVP> getAttributesFromRequest(
			String attributeId) {
		return request.getAVPList(attributeId, true);
	}

	@Override
	public Object getValue(String key) {
		return null;
	}
}
