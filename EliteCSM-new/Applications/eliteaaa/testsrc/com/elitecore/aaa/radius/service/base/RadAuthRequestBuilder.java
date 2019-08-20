package com.elitecore.aaa.radius.service.base;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacketBuilder;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

/**
 * 
 * @author narendra.pathai
 *
 */
public class RadAuthRequestBuilder {

	private RadiusPacketBuilder radiusPacketBuilder = new RadiusPacketBuilder();
	private List<IRadiusAttribute> infoAttributes = Collectionz.newArrayList();
	private Map<String, Object> parameters = new HashMap<String, Object>();
	
	private AAAServerContext serverContext;

	public RadAuthRequestBuilder() {
		this(null);
	}
	
	public RadAuthRequestBuilder(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	public RadAuthRequestBuilder addClassAttribute(String value) {
		IRadiusAttribute classAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(RadiusAttributeConstants.CLASS);
		classAttribute.setStringValue(value);
		radiusPacketBuilder.addAttribute(classAttribute);
		return this;
	}

	public RadAuthRequestBuilder addInfoAttribute(String attributeId,
			String value) throws InvalidAttributeIdException {
		IRadiusAttribute infoAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(attributeId);
		infoAttribute.setStringValue(value);
		infoAttributes.add(infoAttribute);
		return this;
	}

	public RadAuthRequestBuilder addAttribute(String attributeId, String value) throws InvalidAttributeIdException {
		IRadiusAttribute classAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(attributeId);
		classAttribute.setStringValue(value);
		radiusPacketBuilder.addAttribute(classAttribute);
		return this;
	}
	
	public RadAuthRequestBuilder addAttribute(String attributeId, List<String> valueList) throws InvalidAttributeIdException {
		Iterator<String> iterator = valueList.iterator();
		while(iterator.hasNext()) {
			addAttribute(attributeId, iterator.next());
		}
		return this;
	}
	
	public RadAuthRequestBuilder packetType(int packetType) {
		radiusPacketBuilder.packetType(packetType);
		return this;
	}
	
	public RadAuthRequestBuilder addAttribute(IRadiusAttribute attribute) {
		radiusPacketBuilder.addAttribute(attribute);
		return this;
	}
	
	public RadAuthRequestBuilder addParameter(String key, Object value) {
		parameters.put(key, value);
		return this;
	}
	
	public RadAuthRequest build() {
		return buildRequest();
	}
	
	public RadAuthRequest buildRequest() {
		try {
			BaseRadiusAuthService.RadiusAuthRequestImpl radAuthRequest = new BaseRadiusAuthService.RadiusAuthRequestImpl(radiusPacketBuilder.build().getBytes(), InetAddress.getLocalHost(), 0, serverContext, new SocketDetail(InetAddress.getLocalHost().getHostAddress(), 0));
			for (IRadiusAttribute infoAttribute : infoAttributes) {
				radAuthRequest.addInfoAttribute(infoAttribute);
			}
			for (Entry<String, Object> entry : parameters.entrySet()) {
				radAuthRequest.setParameter(entry.getKey(), entry.getValue());
			}
			return radAuthRequest;
		} catch (UnknownHostException ex) {
			throw new AssertionError(ex);
		}
	}
	
	public RadAuthResponse buildResponse() {
		RadiusPacket build = radiusPacketBuilder.build();

		BaseRadiusAuthService.RadiusAuthResponseImpl radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(build.getAuthenticator(), 0, serverContext);
		
		for (IRadiusAttribute attribute : build.getRadiusAttributes()) {
			radAuthResponse.addAttribute(attribute);
		}
		return radAuthResponse;
	}
}
