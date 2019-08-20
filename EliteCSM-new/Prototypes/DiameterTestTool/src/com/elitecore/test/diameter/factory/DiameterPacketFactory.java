package com.elitecore.test.diameter.factory;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.test.exception.InvalidPacketException;
import com.elitecore.test.util.XMLDeserializer;
import com.elitecore.test.dependecy.diameter.packet.DiameterAnswer;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.dependecy.diameter.packet.DiameterRequest;
import com.elitecore.test.diameter.jaxb.DiameterAttributeData;
import com.elitecore.test.diameter.jaxb.DiameterPacketData;

import javax.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class DiameterPacketFactory {
	private DiameterAttributeFactory diameterAttributeFactory;
	private Map<String, DiameterPacket> diameterPacketMap = new HashMap<String, DiameterPacket>();
	
	public DiameterPacketFactory(DiameterAttributeFactory diameterAttributeFactory) {
		super();
		this.diameterAttributeFactory = diameterAttributeFactory;
	}
	
	public DiameterPacketFactory() {
		this.diameterAttributeFactory = new DiameterAttributeFactory();
	}

	public DiameterPacket createDiameterPacket(DiameterPacketData packetData) throws AttributeNotFoundException, InvalidPacketException{
		
		if(packetData == null) {
			throw new InvalidPacketException("Invalid content or file is empty");
		}
		
		DiameterPacket diameterPacket = null;
		if(packetData.getIsRequest()){
			diameterPacket = new DiameterRequest(false);
		} else {
			diameterPacket = new DiameterAnswer();
		}
		
		if (packetData.getCommandCode() <= 0) {
			throw new InvalidPacketException("Invalid command code: " + packetData.getCommandCode() + ", Command code must be positive numeric");
		}
		
		if (packetData.getAppId() < 0) {
			throw new InvalidPacketException("Invalid application id: " + packetData.getAppId() + ", Application id must be positive numeric or 0");
		}
		
		if (Collectionz.isNullOrEmpty(packetData.getAttributeDatas())) {
			throw new InvalidPacketException("No AVP(s) configured");
		}
		
		diameterPacket.setCommandCode(packetData.getCommandCode());
		diameterPacket.setApplicationID(packetData.getAppId());
		
		for(DiameterAttributeData attributeData :  packetData.getAttributeDatas()){
			diameterPacket.addAvp(diameterAttributeFactory.createAVP(attributeData));
		}
		
		return diameterPacket;
	}
	
	public DiameterPacket createDiameterPacket(File file) throws JAXBException, IOException, AttributeNotFoundException, InvalidPacketException{
		DiameterPacket diameterPacket = diameterPacketMap.get(file.getAbsolutePath());
		
		if(diameterPacket != null){
			return diameterPacket;
		}
		
		DiameterPacketData templateData = XMLDeserializer.deserialize(file, DiameterPacketData.class);
		diameterPacket = createDiameterPacket(templateData);
		diameterPacketMap.put(file.getAbsolutePath(), diameterPacket);
		
		return diameterPacket;
	}

}
