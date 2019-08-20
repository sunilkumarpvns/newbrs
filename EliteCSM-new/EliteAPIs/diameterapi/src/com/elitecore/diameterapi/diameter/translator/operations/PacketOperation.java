package com.elitecore.diameterapi.diameter.translator.operations;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.delegator.PacketDelegator;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public abstract class PacketOperation<P, A, G extends A> {

	private static final String MODULE = "PCKT-OP";
	protected OperationData operationData;
	protected PacketDelegator<P, A, G> packetDelegator;
	private String policyName;

	public abstract void execute(TranslatorParams translatorParams);
	
	public PacketOperation(String policyName, OperationData operationData,
			PacketDelegator<P, A, G> packetDelegator) {
		Preconditions.checkNotNull(operationData, "Operation can not be performed, Reason: Operation Data not found");
		this.policyName = policyName;
		this.operationData = operationData; 
		this.packetDelegator = packetDelegator;
	}

	/**
	 * This fetches a literal value from Non Grouped Key.
	 */
	protected final String getDestinationAttributeId(NonGroupedKey key, ValueProvider valueProvider) {
		try {
			return key.getElement().getStringValue(valueProvider);

		} catch (Exception e) {
			throw new NullPointerException("Attribute Id not found, Reason: " + e.getMessage());
		}
	}
	
	/**
	 * Fetch Attributes from parentAttributes / diameterPacket based on Inputs.
	 * 
	 * @param identifier attribute identifier to be fetched.
	 * @param parentAttributes 
	 * @param packet
	 * @param getStrictlyFromParent retrieves Attributes from parentAttributes, 
	 * 			will not be applicable if parentAttributes list is empty
	 *  
	 * @return List of extracted Attributes.
	 */
	protected List<A> fetchAttributes(String identifier, 
			List<A> parentAttributes, P packet, 
			boolean getStrictlyFromParent) {
		
		Preconditions.checkNotNull(identifier, "Can not fetch value, Attribute Identifier not found");
		
		if(Collectionz.isNullOrEmpty(parentAttributes)) {
			LogManager.getLogger().debug(MODULE, "Fetching Attribute-ID: " + identifier + " from Packet");
			return packetDelegator.getAttributeList(packet, identifier);
		}
		if(identifier.startsWith("this")) {

			String [] AttributeIds = packetDelegator.splitAttributeIds(identifier);
			if(AttributeIds.length == 1) {
				return parentAttributes;
			}
			identifier = identifier.substring(5);
			getStrictlyFromParent = true;
		}
		if(getStrictlyFromParent) {
			List<A> subChildAttributes = new ArrayList<A>();

			for(A Attribute : parentAttributes) {

				if(packetDelegator.isAttributeGrouped(Attribute) == false) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Attribute: " + packetDelegator.getAttributeID(Attribute)+ 
								" is not Grouped, Skipping Attribute");
					}
					continue;
				}
				
				@SuppressWarnings("unchecked")
				G groupedAttribute = (G) Attribute;
				List<A> subAttributeList = packetDelegator.getSubAttributesList(groupedAttribute, identifier);
				
				if(Collectionz.isNullOrEmpty(subAttributeList)) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Attribute: " + packetDelegator.getAttributeID(Attribute) + 
								" does not contain " + identifier + " Attribute, Skipping Attribute");
					}
					continue;
				}
				subChildAttributes.addAll(subAttributeList);
			}
			return subChildAttributes;
		}
		return packetDelegator.getAttributeList(packet, identifier);
	}
	
	/**
	 * Gets Innermost Attribute from Attribute ID from an Attribute.
	 * 
	 * examples,
	 * 
	 * 0:456.0:431.0:421  	--> 0:421
	 * 0:296 				--> 0:296 
	 * 
	 */
	protected A getInnermostAttributeFromAttribute(String attributeId, A attribute) {
		String[] attributeIds = packetDelegator.splitAttributeIds(attributeId);
		if(attributeIds.length > 1){
			@SuppressWarnings("unchecked")
			G parent = (G) attribute;
			attributeId = attributeId.substring(attributeId.indexOf(attributeIds[0]) + attributeIds[0].length() + 1);
			return packetDelegator.getSubAttribute(parent, attributeId);
		} else {
			return attribute;
		}
	}
	
	public String getPolicyName() {
		return policyName;
	}
}
