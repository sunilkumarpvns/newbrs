package com.elitecore.diameterapi.diameter.translator.operations;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.translator.delegator.PacketDelegator;
import com.elitecore.diameterapi.diameter.translator.operations.data.HeaderKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.diameterapi.diameter.translator.parser.CopyPacketParser;

/**
 * Upgrade Operation upgrades a Packet. 
 * If Attribute with Attribute Id derived from Destination Key Expression,
 * is arrived in Packet it is Updated and 
 * if it is not arrived in Packet, it will be added with value of Source Expression.
 * 
 * @author monica.lulla
 *
 * @see BasePacketOperation
 */
public class UpgradeOperation<P, A, G extends A> extends BasePacketOperation<P, A, G> {

	private static final String MODULE = "UPGRD-OP";

	public UpgradeOperation(String policyName, OperationData operationData, PacketDelegator<P, A, G> packetDelegator) {
		super(policyName, operationData, packetDelegator);
	}

	private class UpgradeDestinationKeyProcessor implements DestinationKeyProcessor<A> {

		private P packetToOperate;
		private List<AttributeAdder> attributeAdders;
		private List<A> selectedParents;

		public UpgradeDestinationKeyProcessor(P packetToOperate) {
			this.packetToOperate = packetToOperate;
			this.attributeAdders = new ArrayList<AttributeAdder>();
		}

		public UpgradeDestinationKeyProcessor(P packetToOperate,
				List<A> selectedParents) {
			this(packetToOperate);
			this.selectedParents = selectedParents;
		}

		@Override
		public void process() {

			for(AttributeAdder adder : attributeAdders) {
				adder.perfrom();
			}
		}

		@Override
		public List<A> provideParentAttributes(NonGroupedKey destinationKey) {

			// Get Parent Attributes
			List<A> parents = selectConditionalParents(packetToOperate, destinationKey);

			// No Parent Found --> Add Parent
			if(Collectionz.isNullOrEmpty(parents) && destinationKey.getCondition() == null) {

				A Attribute = packetDelegator.createAttribute(destinationKey.getParent(), null);
				if(Attribute == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().debug(MODULE, "Attribute: " + destinationKey.getParent() + 
								" not found in Dictionary");
					}
					return null;
				}
				A targetAttribute = getInnermostAttributeFromAttribute(destinationKey.getParent(), Attribute);
				parents = new ArrayList<A>();
				parents.add(targetAttribute);
				attributeAdders.add(new AttributeAdder(Attribute));
			}
			return parents;
		}

		@Override
		public List<A> provideAttributes(NonGroupedKey destinationKey) {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Fetching Parent Attributes");			
			}
			String destinationAttribute = getDestinationAttributeId(destinationKey, new AttributeValueProvider(packetToOperate));

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Fetching Attribute: " + destinationAttribute + " from Packet");
			}
			//No Parent Available  --> Get Packet Attributes
			if(Collectionz.isNullOrEmpty(selectedParents)) {
				
				return providePacketAttributes(destinationAttribute);
			} 
			//This Attribute --> Get All Selected Parents
			if(CopyPacketParser.THIS.equals(destinationAttribute)){
				return selectedParents;
			}
			if(destinationAttribute.startsWith(CopyPacketParser.THIS)) {
				destinationAttribute = destinationAttribute.substring(destinationAttribute.indexOf('.') + 1);
			}
			//Parent Available --> Provide Child Attributes
			return provideChildAttributes(destinationAttribute);
		}

		private ArrayList<A> provideChildAttributes(
				String destinationAttribute) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Fetching Destination Attributes");			
			}
			ArrayList<A> selectedAttributes = new ArrayList<A>();
			for(A parentAttribute : selectedParents) {
				if(packetDelegator.isAttributeGrouped(parentAttribute) == false) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Unable to fetch Attribute-Id: " + destinationAttribute + ", Reason: Attribute: " + 
								packetDelegator.getAttributeID(parentAttribute) + " is Non-grouped.");
					}
					break;
				}
				@SuppressWarnings("unchecked")
				G groupedAttribute = (G) parentAttribute;
				List<A> childAttributes = packetDelegator.getSubAttributesList(groupedAttribute, destinationAttribute);
				
				if(Collectionz.isNullOrEmpty(childAttributes)) {
					A attribute = packetDelegator.createAttribute(destinationAttribute, groupedAttribute);
					if(attribute == null) {
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
							LogManager.getLogger().debug(MODULE, "Attribute: " + destinationAttribute + 
									" not found in Dictionary");
						}
						return null;
					}
					A targetAttribute = getInnermostAttributeFromAttribute(destinationAttribute, attribute);
					selectedAttributes.add(targetAttribute);
					attributeAdders.add(new AttributeAdder(attribute, groupedAttribute));
				} else {
					selectedAttributes.addAll(childAttributes);
				}
			}
			return selectedAttributes;
		}

		private List<A> providePacketAttributes(
				String destinationAttribute) {
			List<A> selectedAttributes;
			selectedAttributes = packetDelegator.getAttributeList(packetToOperate, destinationAttribute);
			if(!Collectionz.isNullOrEmpty(selectedAttributes)){
				return selectedAttributes;
			}
			A Attribute =  packetDelegator.createAttribute(destinationAttribute, null);
			if(Attribute == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().debug(MODULE, "Attribute: " + destinationAttribute + 
							" not found in Dictionary");
				}
				return null;
			}
			A targetAttribute = getInnermostAttributeFromAttribute(destinationAttribute, Attribute);
			selectedAttributes = new ArrayList<A>();
			
			selectedAttributes.add(targetAttribute);
			attributeAdders.add(new AttributeAdder(Attribute));
			return selectedAttributes;
		}

		private class AttributeAdder {
			
			final private A attribute;
			final private G parent;
			
			public AttributeAdder(A attribute, G parent) {
				this.attribute = attribute;
				this.parent = parent;
			}
			
			public AttributeAdder(A attribute) {
				this.attribute = attribute;
				this.parent = null;
			}
			
			public void perfrom() {
				
				if(packetDelegator.hasValue(attribute) == false) {
					return;
				}
				
				if(parent == null) {
					packetDelegator.addAttributeToPacket(packetToOperate, attribute);
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Adding Attribute: " + packetDelegator.getAttributeID(attribute) + 
								" in Packet");
					}
					
				} else {
					
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Adding Attribute: " + packetDelegator.getAttributeID(attribute) + 
								" in Attribute: " + packetDelegator.getAttributeID(parent));
					}
					packetDelegator.addSubAttributeToGroupedAttribute(parent, attribute);
				}
			}

		}

	}

	@Override
	protected DestinationKeyProcessor<A> createDestinationKeyProcessor(
			P packetToOperate,
			List<A> destinationParents) {
		return new UpgradeDestinationKeyProcessor(packetToOperate, destinationParents);
	}
	
	@Override
	protected DestinationKeyProcessor<A> createDestinationKeyProcessor(
			P packetToOperate) {
		return new UpgradeDestinationKeyProcessor(packetToOperate);
	}

	@Override
	protected void operateHeaderKey(HeaderKey<P> headerKey, P packet, String sourceHeaderValue) {
		if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
			LogManager.getLogger().warn(MODULE, "Packet Header Operation is not supported for Destination Expression: " + 
					headerKey.getElement() + ". Kindly Use Update Operation");
		}
	}

}
