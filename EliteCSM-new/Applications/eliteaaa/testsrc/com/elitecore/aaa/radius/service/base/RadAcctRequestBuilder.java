package com.elitecore.aaa.radius.service.base;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.base.BaseRadiusAcctService.RadiusAcctRequestImpl;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacketBuilder;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

/**
 * 
 * @author narendra.pathai
 *
 */
public class RadAcctRequestBuilder {

	private RadiusPacketBuilder radiusPacketBuilder = new RadiusPacketBuilder();
	private List<IRadiusAttribute> infoAttributes = Collectionz.newArrayList();

	public RadAcctRequestBuilder addClassAttribute(String value) {
		IRadiusAttribute classAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(RadiusAttributeConstants.CLASS);
		classAttribute.setStringValue(value);
		radiusPacketBuilder.addAttribute(classAttribute);
		return this;
	}

	public RadAcctRequestBuilder addInfoAttribute(String attributeId,
			String value) throws InvalidAttributeIdException {
		IRadiusAttribute infoAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(attributeId);
		infoAttribute.setStringValue(value);
		infoAttributes.add(infoAttribute);
		return this;
	}

	public RadAcctRequestBuilder addAttribute(String attributeId, Object value) throws InvalidAttributeIdException {
		IRadiusAttribute classAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(attributeId);
		classAttribute.setStringValue(String.valueOf(value));
		radiusPacketBuilder.addAttribute(classAttribute);
		return this;
	}
	
	public RadAcctRequestBuilder packetType(int packetType) {
		radiusPacketBuilder.packetType(packetType);
		return this;
	}
	
	public RadAcctRequestBuilder addAttribute(IRadiusAttribute attribute) {
		radiusPacketBuilder.addAttribute(attribute);
		return this;
	}
	
	public RadAcctRequest build() {
		try {
			RadiusAcctRequestImpl radAcctRequest = new BaseRadiusAcctService.RadiusAcctRequestImpl(radiusPacketBuilder.build().getBytes(), InetAddress.getLocalHost(), 0, new SocketDetail("0.0.0.0", 0));
			for (IRadiusAttribute infoAttribute : infoAttributes) {
				radAcctRequest.addInfoAttribute(infoAttribute);
			}
			return radAcctRequest;
		} catch (UnknownHostException ex) {
			throw new AssertionError(ex);
		}
	}

	public RadAcctResponse buildResponse(RadServiceRequest request) throws UnknownHostException {

		BaseRadiusAcctService.RadiusAcctResponseImpl response = new BaseRadiusAcctService.RadiusAcctResponseImpl(request.getAuthenticator(), request.getIdentifier());
		for (IRadiusAttribute infoAttribute : infoAttributes) {
			response.addInfoAttribute(infoAttribute);
		}
		return response;

	}
}
