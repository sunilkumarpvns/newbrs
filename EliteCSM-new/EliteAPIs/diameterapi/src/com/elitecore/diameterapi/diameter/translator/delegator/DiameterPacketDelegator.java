package com.elitecore.diameterapi.diameter.translator.delegator;

import java.util.List;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class DiameterPacketDelegator implements PacketDelegator<DiameterPacket, IDiameterAVP, AvpGrouped> {

	private static DiameterPacketDelegator diameterPacketDelegator;
	
	@Override
	public IDiameterAVP createAttribute(String attributeId, IDiameterAVP parentAttribute) {
		if(DiameterUtility.isGroupAvpId(attributeId)){
			return  DiameterUtility.createAvp(attributeId);
		}
		return DiameterDictionary.getInstance().getKnownAttribute(attributeId);
	}

	@Override
	public boolean isAttributeGrouped(IDiameterAVP attribute) {
		return attribute.isGrouped();
	}

	@Override
	public void addSubAttributesToGroupedAttribute(
			AvpGrouped groupedDestinationAVP,
			List<IDiameterAVP> childAVPs) {
		groupedDestinationAVP.addSubAvps(childAVPs);
	}

	@Override
	public void setStringValue(IDiameterAVP attribute, String value) {
		attribute.setStringValue(value);
	}

	@Override
	public String getStringValue(IDiameterAVP attribute) {
		return attribute.getStringValue();
	}

	@Override
	public long getLongValue(IDiameterAVP attributes) {
		return attributes.getInteger();
	}

	@Override
	public IDiameterAVP cloneAttribute(IDiameterAVP avp)
			throws CloneNotSupportedException {
		return (IDiameterAVP) avp.clone();
	}

	@Override
	public String getAttributeID(IDiameterAVP attribute) {
		return attribute.getAVPId();
	}

	@Override
	public List<IDiameterAVP> getAttributeList(DiameterPacket packet,
			String attributeID) {
		return packet.getAVPList(attributeID, DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
	}

	@Override
	public List<IDiameterAVP> getSubAttributesList(
			AvpGrouped groupedAttribute, String attributeId) {
		return groupedAttribute.getSubAttributeList(attributeId);
	}

	@Override
	public String[] splitAttributeIds(String attributeId) {
		return DiameterUtility.diaAVPIdSplitter.splitToArray(attributeId);
	}

	@Override
	public void addAttributesToPacket(DiameterPacket packet,
			List<IDiameterAVP> destinationAVPs) {
		packet.addAvps(destinationAVPs);
	}

	@Override
	public List<IDiameterAVP> getSubAttributesList(AvpGrouped groupedAttribute) {
		return groupedAttribute.getGroupedAvp();
	}

	@Override
	public boolean isKeyValueSupported() {
		return false;
	}

	@Override
	public String getKeyValue(IDiameterAVP attribute, String key) {
		return null;
	}

	@Override
	public ValueProvider getValueProvider(DiameterPacket packet) {
		return new DiameterAVPValueProvider(packet);
	}

	@Override
	public void remove(DiameterPacket packet, IDiameterAVP attribute) {
		packet.removeAVP(attribute, DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
	}

	public static DiameterPacketDelegator getInstance() {
		if(diameterPacketDelegator == null){
			diameterPacketDelegator = new DiameterPacketDelegator();
		}
		return diameterPacketDelegator;
	}

	@Override
	public void addAttributeToPacket(DiameterPacket packet,
			IDiameterAVP attribute) {
		packet.addAvp(attribute);
	}

	@Override
	public void addSubAttributeToGroupedAttribute(
			AvpGrouped groupAttribute, IDiameterAVP subAttribute) {
		groupAttribute.addSubAvp(subAttribute);
	}

	@Override
	public boolean hasValue(IDiameterAVP attribute) {
		
		boolean valueAvailable = attribute.getVendorId() > 0 ? attribute.getLength() > 12 : attribute.getLength() > 8;
		if(valueAvailable == false) {
			return false;
		}
		/**
		 * To verify all of the Inner AVP of the Group AVP contain some value. 
		 */
		if(attribute.isGrouped()) {
			for(IDiameterAVP avp : attribute.getGroupedAvp()) {
				if(hasValue(avp) == false){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public IDiameterAVP getAttribute(DiameterPacket packet, String attributeID) {
		return packet.getAVP(attributeID, DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
	}

	@Override
	public IDiameterAVP getSubAttribute(AvpGrouped groupedAttribute,
			String attributId) {
		return groupedAttribute.getSubAttribute(attributId);
	}
}
