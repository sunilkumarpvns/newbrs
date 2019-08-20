package com.elitecore.aaa.radius.service.assertion;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.hamcrest.Matcher;

import com.elitecore.aaa.EliteAAAMatchers;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;

@XmlRootElement(name = "packet-type")
public class PacketTypeAssertion implements PacketAssertion {

	private int packetType;
	
	@XmlAttribute(name = "value")
	public int getPacketType() {
		return packetType;
	}

	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}

	@Override
	public Matcher<? super RadServiceRequest> createRequestMatcher() {
		return EliteAAAMatchers.RadServiceRequestMatchers.packetType(packetType);
	}

	@Override
	public Matcher<? super RadServiceResponse> createResponseMatcher() {
		return EliteAAAMatchers.RadServiceResponseMatchers.packetType(packetType);
	}
}
