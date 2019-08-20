package com.elitecore.diameterapi.diameter.translator.operations;

import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.delegator.PacketDelegator;
import com.elitecore.diameterapi.diameter.translator.operations.data.AttributeMapping;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.exprlib.parser.expression.IdentifierExpression;

/**
 * Move Operation is derivative of Upgrade Operation, 
 * additionally it removes Source Attribute, whose value is consumed.
 * 
 * Limitation:
 * 
 * 1. It may be possible that it is not possible to remove Attribute,
 * in cases of dotted Attribute Notation
 * 
 * example,
 * 
 * {with=0:456.0:431, do = this}  
 * 
 * Here in such cases it is not possible to locate exact Attribute for removal, 
 * due to Attribute Wrapping by this.
 * 
 * Work Around: Configure as below 
 * 
 *  {with=0:456, do = this.0:431}  
 * 
 * 2. Besides, Move Attribute is limited to Identifier Expression (Attributes) only.
 * 
 * Work Around: Use Upgrade + Remove Operation
 *  
 * @author monica.lulla
 * 
 * @see UpgradeOperation
 *
 */
public class MoveOperation<P, A, G extends A> extends UpgradeOperation<P, A, G> {

	private static final String MODULE = "MV-OP";

	public MoveOperation(String policyName, OperationData operationData, PacketDelegator<P, A, G> packetDelegator) {
		super(policyName, operationData, packetDelegator);
	}

	@Override
	protected void operateNonGroupedSourceKey(TranslatorParams translatorParams,
			P packetToOperate, AttributeMapping attributeMapping,
			List<A> sourceParents,
			List<A> destAttributes) {

		NonGroupedKey sourceKey = (NonGroupedKey) attributeMapping.getSourceKey();
		AttributePair parentChildPair = getParentChildAttribute(sourceParents, 
				sourceKey, 
				packetToOperate);
		
		A sourceAttribute = parentChildPair.getAttribute();
		G parentAttribute = parentChildPair.getParent();

		String sourceAttributeValue = null;
		if(sourceAttribute != null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Moving Attribute: " + packetDelegator.getAttributeID(sourceAttribute));
			}
			sourceAttributeValue = sourceKey.getValue(packetDelegator.getStringValue(sourceAttribute));
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Source Value not found, getting Default Value");
			}
			sourceAttributeValue = sourceKey.getDefaultValue();
		}

		if(sourceAttributeValue == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Operation not performed, Reason: Source Value not found");
			}
			return;
		}
		
		boolean markAttributeForRemoval = false;
		//Assigning Attribute values
		for(int i = 0  ; i< destAttributes.size() ; i++) {
			try {
				packetDelegator.setStringValue(destAttributes.get(i), sourceAttributeValue);
				markAttributeForRemoval = true;
			}catch(Exception e){

				LogManager.getLogger().trace(e);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Unable to process attribute: " + 
							packetDelegator.getAttributeID(destAttributes.get(i)) + ", Reason: " + e.getMessage());
				}
			}
		}
		if (markAttributeForRemoval && sourceAttribute != null) {
			if (parentAttribute == null) {

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Removing Attribute: " + packetDelegator.getAttributeID(sourceAttribute) + 
							" from Packet");
				}
				packetDelegator.remove(packetToOperate, sourceAttribute);
			} else {

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Removing Attribute: " + packetDelegator.getAttributeID(sourceAttribute) + 
							" from Attribute: " + packetDelegator.getAttributeID(parentAttribute));
				}

				List<A> subAttributes = packetDelegator.getSubAttributesList((G)parentAttribute);
				subAttributes.remove(sourceAttribute);
			}
		}
	}

	private class AttributePair {
		private A attribute;
		private G parent;
		
		public AttributePair(A attribute, G parent) {
			this.attribute = attribute;
			this.parent = parent;
		}

		public A getAttribute() {
			return attribute;
		}

		public G getParent() {
			return parent;
		}

	}
	@SuppressWarnings("unchecked")
	private AttributePair getParentChildAttribute(List<A> sourceParents, NonGroupedKey sourceKey, P packetToOperate) {

		A sourceAttribute = null;
		G parentAttribute = null;
		
		/*
		 * Move Operation is applicable only to Identifiers. i.e. Attribute IDs
		 */
		if(sourceKey.getElement() instanceof IdentifierExpression == false) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Can not fetch source Attributes for operation, Reason: configured Source Attribute-Id is not an Identifier (VendorID:AttributeID)");
			}
			return new AttributePair(null, null);
		}
		String sourceAttributeId = sourceKey.getElement().getName();

		/*
		 * Source Attribute Id not found.
		 */
		if(sourceAttributeId == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Can not fetch source Attributes for operation, Reason: Source Attribute-Id not found");
			}
			return new AttributePair(null, null);
		} 

		String [] attributeIds = packetDelegator.splitAttributeIds(sourceAttributeId);

		if(Collectionz.isNullOrEmpty(sourceParents)) {
			LogManager.getLogger().debug(MODULE, "Fetching Attribute-ID: " + sourceAttributeId + " from Packet");

			// Attribute of Type 10415:1001 
			if (attributeIds.length == 1) {
				sourceAttribute = packetDelegator.getAttribute(packetToOperate, sourceAttributeId);
				return new AttributePair(sourceAttribute, parentAttribute);
			}
			//Attribute of Type 10415:1001.10415:1004
			sourceParents = packetDelegator.getAttributeList(packetToOperate, attributeIds[0]);
			if(Collectionz.isNullOrEmpty(sourceParents)) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "No Parent Attributes with Attribute-ID: " + 
							attributeIds[0] + " found from Packet");
				}
				/*
				 * Here, 10415:1001 i.e. Parent attribute is not found, so no child can be fetched. 
				 * hence returning null.
				 */
				return new AttributePair(null, null);
			}


		} else {

			if (attributeIds.length == 1) {
				sourceAttribute = sourceParents.get(0);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Attribute: " + packetDelegator.getAttributeID(sourceAttribute) + 
							" will be removed only if available in Packet");
				}
				return new AttributePair(sourceAttribute, parentAttribute);
			}
		}
		for(A attribute : sourceParents) {

			if(packetDelegator.isAttributeGrouped(attribute) == false) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Attribute: " + packetDelegator.getAttributeID(attribute) + 
							" is not Grouped, Skipping Attribute");
				}
				continue;
			}
			G groupedAttribute = (G) attribute;

			for(int i = 1 ; i < attributeIds.length -1 ; i++ ) {
				groupedAttribute = (G) packetDelegator.getSubAttribute(groupedAttribute, attributeIds[i]); 
				if(groupedAttribute == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Attribute: " + attributeIds[i] + 
								" not found, Skipping Attribute");
					}
					break;
				}
			}
			if(groupedAttribute != null) {
				parentAttribute = groupedAttribute;
				sourceAttribute = packetDelegator.getSubAttribute(parentAttribute, attributeIds[attributeIds.length -1]);
			}
			if(sourceAttribute != null) {
				break;
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Attribute: " + packetDelegator.getAttributeID(attribute) + 
						" does not contain " + sourceAttributeId + " Attribute, Skipping Attribute");
			}
		}
		return new AttributePair(sourceAttribute, parentAttribute);
	}


}
