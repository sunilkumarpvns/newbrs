package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Optional;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;
import com.elitecore.test.dependecy.diameter.packet.avps.grouped.AvpGrouped;
import com.elitecore.test.diameter.jaxb.AttributeData;

public class ValidationCommand implements Command {

	private static final String MODULE = "VALIDATE-CMD";
	private static final String ANY_VALUE = "*";
	private List<AttributeData> attributeDatas;
	private String name;

	public ValidationCommand(List<AttributeData> attributeDatas, String name) {
		this.attributeDatas = attributeDatas;
		this.name = name;
		
	}

	@Override
	public void execute(ExecutionContext context) throws Exception {
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing validate("+getName()+") command");
		
		if(Collectionz.isNullOrEmpty(attributeDatas) == false){
			Optional<Object> optional = context.get(ReceivePacketContext.RCVD_REQ);
			if(optional.isPresent() == false){
				fail("Diameter Packet not found for check");
			}
			
			DiameterPacket diameterPacket = (DiameterPacket) optional.get();
			
			for (AttributeData attributeData : attributeDatas) {
				checkAttribute(attributeData, diameterPacket);
			}		
		}

	}
	
	
	private void checkAttribute(AttributeData attributeData, DiameterPacket diameterPacket){
		IDiameterAVP avp = diameterPacket.getAVP(attributeData.getId());
		assertNotNull(attributeData.getId()+ " not found from packet",avp);
		if(attributeData.getValue() == null){
			if(Collectionz.isNullOrEmpty(attributeData.getAttributeDatas())){
				fail("invalid configuration neighther value or child attribut provided for attribute"+ attributeData.getId());
			}
			
			if((avp instanceof AvpGrouped) == false){
				fail(attributeData.getId()+ " is not grouped AVP");
			}
			
			for(AttributeData attributeData2 : attributeData.getAttributeDatas()){
				checkAttribute(attributeData2, (AvpGrouped)avp);
			}
			
		} else {
			
			if(ANY_VALUE.equals(attributeData.getValue()) == false){
				assertTrue("received value:" + avp.getStringValue() +", expected value:" + attributeData.getValue(),avp.getStringValue().equalsIgnoreCase(attributeData.getValue()));
			}
			
		}
	}
	
	private void checkAttribute(AttributeData attributeData, AvpGrouped avp){
		
		IDiameterAVP childAVP = avp.getSubAttribute(attributeData.getId());
		assertNotNull(attributeData.getId()+ " not found from packet",childAVP);
		
		if(attributeData.getValue() == null){
			
			if(Collectionz.isNullOrEmpty(attributeData.getAttributeDatas())){
				fail("invalid configuration neighther value or child attribut provided for attribute"+ attributeData.getId());
			}
			
			if((avp instanceof AvpGrouped) == false){
				fail(attributeData.getId()+ " is not grouped AVP");
			}
			
			for(AttributeData attributeData2 : attributeData.getAttributeDatas()){
				checkAttribute(attributeData2, (AvpGrouped)avp);
			}
			
		} else {
			if(ANY_VALUE.equals(attributeData.getValue()) == false){
				assertTrue("received value:" + childAVP.getStringValue() +", expected value:" + attributeData.getValue(),childAVP.getStringValue().equalsIgnoreCase(attributeData.getValue()));
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

}
