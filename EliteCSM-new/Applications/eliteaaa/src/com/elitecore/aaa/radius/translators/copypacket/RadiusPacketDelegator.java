package com.elitecore.aaa.radius.translators.copypacket;

import java.util.List;

import com.elitecore.aaa.radius.translators.RadiusAttributeValueProvider;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusGroupedAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.diameterapi.diameter.translator.delegator.PacketDelegator;
import com.elitecore.diameterapi.diameter.translator.parser.CopyPacketParser;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class RadiusPacketDelegator  implements PacketDelegator<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>{

	private static final String MODULE = "RAD-PKT-DELEGATOR";
	private static RadiusPacketDelegator radiusPacketDelegator = null;
	@Override
	public IRadiusAttribute createAttribute(String attributeId, 
			IRadiusAttribute parentAttribute) {

		//Check if Attribute ID is of Type Grouped
		int levels;
		try {
			levels = Dictionary.getInstance().getAttributeId(attributeId).getAttributeLevel();
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().trace(e);
			LogManager.getLogger().warn(MODULE, "Invalid Attribute-ID: " + attributeId + 
					", Reason: " + e.getMessage());
			return null;
		}
		
		// To Create Non-Group AVP
		if(levels == 1){
			return Dictionary.getInstance().getKnownAttribute(parentAttribute != null ? 
					parentAttribute.getParentId() + ":" + attributeId : attributeId );
		}
		
		//To Create Group Attribute
		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(attributeId);
		if(attribute == null) {
			return null;
		}
		IRadiusGroupedAttribute mainAttribute  = null;
		
		for(int i = 1 ; i < levels ; i++) {
			attributeId = attributeId.substring(0, attributeId.lastIndexOf(':'));
			mainAttribute = (IRadiusGroupedAttribute) Dictionary.getInstance().getKnownAttribute(attributeId);
			if(mainAttribute == null) {
				return null;
			}
			mainAttribute.addSubAttributes(attribute);
			attribute = mainAttribute;
		}
		return mainAttribute;
		
	}

	@Override
	public boolean isAttributeGrouped(IRadiusAttribute attribute) {
		return attribute instanceof IRadiusGroupedAttribute; 
	}

	@Override
	public void addSubAttributesToGroupedAttribute(
			IRadiusGroupedAttribute groupedDestinationAVP,
			List<IRadiusAttribute> childAVPs) {
		if(childAVPs == null){
			return;
		}
		groupedDestinationAVP.addSubAttributes(childAVPs.toArray(new IRadiusAttribute[childAVPs.size()]));
	}

	@Override
	public void setStringValue(IRadiusAttribute attribute,
			String value) {
		attribute.setStringValue(value);
	}

	@Override
	public String getStringValue(IRadiusAttribute attribute) {
		return attribute.getStringValue();
	}

	@Override
	public long getLongValue(IRadiusAttribute attributes) {
		return attributes.getLongValue();
	}

	@Override
	public IRadiusAttribute cloneAttribute(IRadiusAttribute avp)
			throws CloneNotSupportedException {
		return (IRadiusAttribute) avp.clone();
	}

	@Override
	public String getAttributeID(IRadiusAttribute attribute) {
		return attribute.getIDString();
	}

	@Override
	public List<IRadiusAttribute> getAttributeList(RadiusPacket packet,
			String attributeID) {
		return (List<IRadiusAttribute>) packet.getRadiusAttributes(attributeID, true);
	}

	@Override
	public List<IRadiusAttribute> getSubAttributesList(
			IRadiusGroupedAttribute groupedAttribute, String attributeId) {
		String[] avpIds = RadiusUtility.radAttributeIdSplitter.splitToArray(attributeId);
		int [] intIds = new int[avpIds.length];
		for(int i = 0 ; i< avpIds.length ; i++){
			intIds[i] = Integer.parseInt(avpIds[i]);
		}
		return (List<IRadiusAttribute>) groupedAttribute.getSubAttributes(intIds);
	}

	@Override
	public String[] splitAttributeIds(String attributeId) {

		if(attributeId.startsWith(CopyPacketParser.THIS)){
			return RadiusUtility.radAttributeIdSplitter.splitToArray(attributeId);
		}
		int index = attributeId.indexOf(':');
		String[] avpIds = RadiusUtility.radAttributeIdSplitter.splitToArray(attributeId.substring(index + 1));
		avpIds[0] = avpIds.length == 1 ? attributeId : attributeId.substring(0, attributeId.indexOf(':', index+ 1));
		
		return avpIds;
	}

	@Override
	public void addAttributesToPacket(RadiusPacket packet,
			List<IRadiusAttribute> destinationAVPs) {

		packet.addAttributes(destinationAVPs);
	}

	@Override
	public List<IRadiusAttribute> getSubAttributesList(
			IRadiusGroupedAttribute groupedAttribute) {
		return (List<IRadiusAttribute>) groupedAttribute.getAttributes();
	}

	@Override
	public boolean isKeyValueSupported() {
		return true;
	}

	@Override
	public String getKeyValue(IRadiusAttribute attribute, String key) {
		return attribute.getKeyValue(key);
	}

	@Override
	public ValueProvider getValueProvider(RadiusPacket packet) {
		return new RadiusAttributeValueProvider(packet);
	}

	@Override
	public void remove(RadiusPacket packet, IRadiusAttribute attribute) {
		packet.removeAttribute(attribute);
		packet.removeInfoAttribute(attribute);
	}

	@Override
	public void addAttributeToPacket(RadiusPacket packet,
			IRadiusAttribute attribute) {
		packet.addAttribute(attribute);
		
	}

	@Override
	public void addSubAttributeToGroupedAttribute(
			IRadiusGroupedAttribute groupAttribute,
			IRadiusAttribute subAttribute) {
		groupAttribute.addSubAttributes(subAttribute);
	}

	@Override
	public boolean hasValue(IRadiusAttribute attribute) {
		return attribute.getValueBytes() != null && attribute.getValueBytes().length > 0;
	}

	@Override
	public IRadiusAttribute getAttribute(RadiusPacket packet,
			String attributeID) {
		return packet.getRadiusAttribute(attributeID, true);
	}

	@Override
	public IRadiusAttribute getSubAttribute(
			IRadiusGroupedAttribute groupedAttribute, String identifier) {
		
		String[] avpIds = RadiusUtility.radAttributeIdSplitter.splitToArray(identifier);
		int [] intIds = new int[avpIds.length];
		for(int i = 0 ; i< avpIds.length ; i++){
			intIds[i] = Integer.parseInt(avpIds[i]);
		}
		return groupedAttribute.getSubAttribute(intIds);
	}
	
	public static RadiusPacketDelegator getInstance() {
		if(radiusPacketDelegator == null){
			radiusPacketDelegator = new RadiusPacketDelegator();
		}
		return radiusPacketDelegator;
	}

}