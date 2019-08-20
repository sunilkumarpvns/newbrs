package com.elitecore.test.diameter.factory;

import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.test.util.XMLDeserializer;
import com.elitecore.test.dependecy.diameter.Application;
import com.elitecore.test.dependecy.diameter.ApplicationIdentifier;
import com.elitecore.test.dependecy.diameter.packet.CommandCode;
import com.elitecore.test.dependecy.diameter.packet.DiameterAnswer;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.dependecy.diameter.packet.DiameterRequest;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;
import com.elitecore.test.diameter.jaxb.AttributeData;
import com.elitecore.test.diameter.jaxb.PacketData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PacketFactory {
	private AttributeFactory attributeFactory;
	private Map<String, DiameterPacket> diameterPacketMap = new HashMap<String, DiameterPacket>();
	
	public PacketFactory(AttributeFactory attributeFactory) {
		super();
		this.attributeFactory = attributeFactory;
	}
	
	public PacketFactory() {
		this.attributeFactory = new AttributeFactory();
	}

	public DiameterPacket createDiameterPacket(PacketData packetData,ValueProvider  valueProvider) throws Exception{
		
		DiameterPacket diameterPacket = null;
		
		if(packetData.getIsRequest()){
			diameterPacket = new DiameterRequest(false);
		} else {
			diameterPacket = new DiameterAnswer();
		}
		
		CommandCode commandCode = CommandCode.fromDisplayName(packetData.getCommandCode());
		if(commandCode == null) {
			int cc;
			try {				
				cc = Integer.parseInt(packetData.getCommandCode());
			}catch(NumberFormatException ex) {
				throw new Exception("Invalid value received for command-code:" + packetData.getAppId());
			}
			diameterPacket.setCommandCode(cc);
		} else {			
			diameterPacket.setCommandCode(commandCode.code);
		}
		
		
		
		
		ApplicationIdentifier applicationIdentifier = ApplicationIdentifier.fromDisplayName(packetData.getAppId());
		if(applicationIdentifier == null) {
			int appId;
			try {				
				appId = Integer.parseInt(packetData.getAppId());
			}catch(NumberFormatException ex) {
				throw new Exception("Invalid value received for application-id:" + packetData.getAppId());
			}
			diameterPacket.setApplicationID(appId);
		} else {			
			diameterPacket.setApplicationID(applicationIdentifier.applicationId);
		}
		
		diameterPacket.setHop_by_hopIdentifier(packetData.getH2h());
		diameterPacket.setEnd_to_endIdentifier(packetData.getE2e());
		
		
		for(AttributeData attributeData :  packetData.getAttributeDatas()){
			IDiameterAVP diameterAVP = attributeFactory.createAVP(attributeData,valueProvider);
			if(diameterAVP != null) {				
				diameterPacket.addAvp(diameterAVP);
			}
		}
		
		return diameterPacket;
	}
	
	public DiameterPacket createDiameterPacket(File file,ValueProvider valueProvider) throws Exception{
		
		DiameterPacket diameterPacket = diameterPacketMap.get(file.getAbsoluteFile());
		
		if(diameterPacket != null){
			return diameterPacket;
		}
		
		PacketData templeatData = XMLDeserializer.deserialize(file, PacketData.class);
		
		diameterPacket = createDiameterPacket(templeatData,valueProvider);
		
		diameterPacketMap.put(file.getAbsolutePath(), diameterPacket);
		
		diameterPacket.setHop_by_hopIdentifier(templeatData.getH2h());
		diameterPacket.setEnd_to_endIdentifier(templeatData.getE2e());
		
		return diameterPacket;
	}

}
