package com.elitecore.aaa.core.radius.packet.jaxb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.commons.util.exception.AttributeNotFoundException;
import com.elitecore.aaa.core.commons.util.exception.InvalidPacketException;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.RadAcctRequestBuilder;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacketBuilder;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;

@XmlRootElement(name = "radius-packet")
public class RadiusPacketData{

	private int packetType;
	private List<RadiusAttributeData> attributeDatas = new ArrayList<RadiusAttributeData>();
	
	private List<RadiusAttributeData> infoAttributeDatas = new ArrayList<RadiusAttributeData>();

	public RadiusPacketData() {
		attributeDatas = new ArrayList<RadiusAttributeData>();
		infoAttributeDatas = new ArrayList<RadiusAttributeData>();
	}

	@XmlAttribute(name = "type",required=true)
	public int getPacketType() {
		return packetType;
	}

	@XmlElement(name = "attribute")
	public List<RadiusAttributeData> getAttributeDatas() {
		return attributeDatas;
	}

	@XmlElementWrapper(name = "info")
	@XmlElement(name = "attribute")
	public List<RadiusAttributeData> getInfoAttributeDatas() {
		return infoAttributeDatas;
	}
	
	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}

	public RadServiceRequest create(RadAuthRequestBuilder radAuthRequestBuilder) throws AttributeNotFoundException, InvalidPacketException, InvalidAttributeIdException {

		if (getPacketType() <= 0) {
			throw new InvalidPacketException("Invalid Packet-Type: " + getPacketType() + 
					", packet type must be positive numeric");
		}

		radAuthRequestBuilder.packetType(getPacketType());

		for (RadiusAttributeData attributeData : getAttributeDatas()) {
			radAuthRequestBuilder.addAttribute(attributeData.create());
		}
		
		for (RadiusAttributeData attributeData : getInfoAttributeDatas()) {
			radAuthRequestBuilder.addInfoAttribute(attributeData.getId(), attributeData.getValue());
		}
		
		return radAuthRequestBuilder.build();
	}

	
	public RadiusPacket create(RadiusPacketBuilder packetBuilder) throws AttributeNotFoundException, InvalidPacketException {

		if (getPacketType() <= 0) {
			throw new InvalidPacketException("Invalid Packet-Type: " + getPacketType() + 
					", packet type must be positive numeric");
		}

		packetBuilder.packetType(getPacketType());

		for (RadiusAttributeData attributeData : getAttributeDatas()) {
			packetBuilder.addAttribute(attributeData.create());
		}
		
		return packetBuilder.build();
	}
	
	public RadServiceResponse createResponse(RadAuthRequestBuilder radAuthRequestBuilder) throws AttributeNotFoundException, InvalidPacketException {

		if (getPacketType() <= 0) {
			throw new InvalidPacketException("Invalid Packet-Type: " + getPacketType() + 
					", packet type must be positive numeric");
		}

		radAuthRequestBuilder.packetType(getPacketType());

		for (RadiusAttributeData attributeData : getAttributeDatas()) {
			radAuthRequestBuilder.addAttribute(attributeData.create());
		}
		
		return radAuthRequestBuilder.buildResponse();
	}
	
	public RadServiceRequest createAcctRequest(RadAcctRequestBuilder radAcctRequestBuilder) throws InvalidPacketException, AttributeNotFoundException, InvalidAttributeIdException{
		
		if (getPacketType() <= 0) {
			throw new InvalidPacketException("Invalid Packet-Type: " + getPacketType() + 
					", packet type must be positive numeric");
		}
		
		radAcctRequestBuilder.packetType(getPacketType());
		
		for (RadiusAttributeData attributeData : getAttributeDatas()) {
			radAcctRequestBuilder.addAttribute(attributeData.create());
		}
		
		for (RadiusAttributeData attributeData : getInfoAttributeDatas()) {
			radAcctRequestBuilder.addInfoAttribute(attributeData.getId(), attributeData.getValue());
		}
		
		return radAcctRequestBuilder.build();
	}

	public RadServiceResponse createAcctResponse(RadAcctRequestBuilder radAcctRequestBuilder) throws AttributeNotFoundException, InvalidPacketException, UnknownHostException, InvalidAttributeIdException {
		
		if (getPacketType() <= 0) {
			throw new InvalidPacketException("Invalid Packet-Type: " + getPacketType() + 
					", packet type must be positive numeric");
		}
		
		radAcctRequestBuilder.packetType(getPacketType());
		
		for (RadiusAttributeData attributeData : getAttributeDatas()) {
			radAcctRequestBuilder.addInfoAttribute(attributeData.getId(),attributeData.getValue());
		}
		
		RadServiceRequest rsr = radAcctRequestBuilder.build();
		
		return  radAcctRequestBuilder.buildResponse(rsr);
	}
}