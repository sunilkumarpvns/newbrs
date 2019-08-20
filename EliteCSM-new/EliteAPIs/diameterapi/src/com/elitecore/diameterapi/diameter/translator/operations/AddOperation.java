package com.elitecore.diameterapi.diameter.translator.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.delegator.PacketDelegator;
import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.operations.data.AttributeMapping;
import com.elitecore.diameterapi.diameter.translator.operations.data.GroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.Key;
import com.elitecore.diameterapi.diameter.translator.operations.data.KeyTypes;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.IdentifierExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * This Operation adds an Attribute to Packet. 
 * This is applied only when Source Attribute is arrived in Packet.
 * 
 * This operation is guarded with a Pre Check Condition on Operation level.
 * Operation will fail if Condition Fails.
 * 
 * Besides, Source key is associated with a Check Condition,
 * A Destination attribute will be added with the Value of SourceKey,
 * on satisfying the Source Check Condition.
 * 
 * Multiple Attributes will be added if Multiple Values for Source Key is Available.
 * 
 * Operation will fail if this is Unable to find appropriate Values.
 * 
 * @author monica.lulla
 *
 * @param <P> is Packet Type to be operated 
 * @param <A> is Attribute Type to be operated
 * @param <G> is Grouped Attribute Type to be operated 
 */
public class AddOperation<P, A, G extends A> extends PacketOperation<P, A, G> {

	private static final String MODULE = "ADD-OP";
	public AddOperation(String policyName, OperationData operationData, PacketDelegator<P, A, G> packetDelegator) {
		super(policyName, operationData, packetDelegator);
	}

	@Override
	public void execute(TranslatorParams translatorParams) {

		@SuppressWarnings("unchecked")
		P packetToOperate = (P) translatorParams.getParam(TranslatorConstants.TO_PACKET);

		//Checking for Base Condition
		if(operationData.getCheckExpression() != null && 
				operationData.getCheckExpression().evaluate( new AddValueProvider(packetToOperate) ) == false) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Check Expression failed");
			}
			return;
		}
		
		AttributeMapping attributeMapping = operationData.getAttributeMapping();
		if(attributeMapping.getDestinationKey().getKeyType() == KeyTypes.HEADER){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Packet Header Operation is not supported for Destination Expression: " + 
						attributeMapping.getDestinationKey().getElement() + ". Kindly Use Update Operation");
			}
			return;
		}

		//Get Parented Source Attributes
		List<A> sourceParents = null;
		if(attributeMapping.getSourceKey().getParent() != null) { 

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Fetching Source Parent Attributes");
			}
			sourceParents = selectConditionalParents(packetToOperate, 
					attributeMapping.getSourceKey());

			if(Collectionz.isNullOrEmpty(sourceParents)) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Unable to fetch Attributes for:  " + 
							attributeMapping.getSourceKey().getParent());
				}
				return;
			}
		}
		operateAttributeMapping(translatorParams, packetToOperate,
				attributeMapping, sourceParents, null);
	}

	/**
	 * Adds Destination Attributes to Packet
	 * 
	 * @param packetToOperate
	 * @param destinationAttributes
	 * @param attributeMapping
	 */
	private void performOperation(P packetToOperate,
			List<A> destinationAttributes, G destinationParent) {
		
		if(Collectionz.isNullOrEmpty(destinationAttributes)){
			return;
		}		
		if(destinationParent != null){
			packetDelegator.addSubAttributesToGroupedAttribute(destinationParent, destinationAttributes);
		} else {
			packetDelegator.addAttributesToPacket(packetToOperate, destinationAttributes);
		}
	}

	/**
	 * Creates Destination Attributes based on Attribute Mapping.
	 * 
	 * @param translatorParams
	 * @param packetToOperate
	 * @param attributeMapping
	 * @param sourceParents
	 * @return Destination Attributes formed by Attribute Mapping
	 */
	private void operateAttributeMapping(TranslatorParams translatorParams,
			P packetToOperate, AttributeMapping attributeMapping, 
			List<A> sourceParents, G destinationParent) {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Processing Attribute Mapping");
		}
		String destinationKey = getDestinationAttributeId(
				(NonGroupedKey) attributeMapping.getDestinationKey(), 
				new AddValueProvider(packetToOperate));

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Processing Destination Attribute: " + destinationKey);
		}
		
		A destinationAttribute = packetDelegator.createAttribute(destinationKey, destinationParent);

		if(destinationAttribute == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().debug(MODULE, "Operation not performed, Reason: Attribute: " + destinationKey + 
						" not found in Dictionary");
			}
			return;
		}
		// if Destination Key is of Type AttributeGrouped and and Source Key is Grouped
		if(attributeMapping.getSourceKey().getKeyType() == KeyTypes.GROUPED && 
				packetDelegator.isAttributeGrouped(destinationAttribute)) {

			List<A> destinationAttributes =  processGroupedKey(translatorParams, packetToOperate,
					attributeMapping, sourceParents, destinationAttribute);
			performOperation(packetToOperate, destinationAttributes, destinationParent);
			return;
		} 
		// Source Key is Non-Grouped 
		if(attributeMapping.getSourceKey().getKeyType() == KeyTypes.NONGROUPED) {

			List<A> destinationAttributes =  processNonGroupedKey(translatorParams, packetToOperate,
					attributeMapping, sourceParents, destinationAttribute);
			performOperation(packetToOperate, destinationAttributes, destinationParent);
			return;
		}

		String destinationType = packetDelegator.isAttributeGrouped(destinationAttribute) ? "GROUPED" : "NONGROUPED";
		String sourceType = attributeMapping.getSourceKey().getKeyType().toString();

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Operation not performed, Reason: Incompatible Source("+ 
					sourceType +") and Destination("+destinationType+") Attribute Type");
		}
	}

	private List<A> processGroupedKey(TranslatorParams translatorParams,
			P packetToOperate, AttributeMapping attributeMapping,
			List<A> sourceParents, A destinationAttribute) {
		@SuppressWarnings("unchecked")
		G groupedDestinationAttribute = (G) getInnermostAttributeFromAttribute(
				getDestinationAttributeId((NonGroupedKey)attributeMapping.getDestinationKey(), 
						new AddValueProvider(packetToOperate)), destinationAttribute);

		GroupedKey sourceKey =  (GroupedKey) attributeMapping.getSourceKey();

		for(AttributeMapping mapping : sourceKey.getElement()) {

			List<A> selectedSourceParentAttributes = new ArrayList<A>();

			// Source Key is Conditional
			if(mapping.getSourceKey().getParent() != null) { 

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Fetching Source Parent Attributes");
				}
				List<A> sourceParentAttributes = selectConditionalParents(packetToOperate, mapping.getSourceKey());

				// Selective Parents not Available --> Can not Proceed with this Mapping
				if(Collectionz.isNullOrEmpty(sourceParentAttributes)) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Unable to fetch Attributes for:  " + 
								mapping.getSourceKey().getParent());
					}
					continue;
				}
				// Add Source Attributes of Current Attribute Mapping
				selectedSourceParentAttributes.addAll(sourceParentAttributes);
			} 

			// Add Source Attributes of Previous Attribute Mapping
			if(!Collectionz.isNullOrEmpty(sourceParents)) {
				selectedSourceParentAttributes.addAll(sourceParents);
			}
			
			//Get Child Attributes
			operateAttributeMapping(translatorParams, packetToOperate, mapping, selectedSourceParentAttributes, groupedDestinationAttribute);
		} 
		
		if(Collectionz.isNullOrEmpty(packetDelegator.getSubAttributesList(groupedDestinationAttribute))){
			return null;
		}
		List<A> destAttributes = new ArrayList<A>();
		destAttributes.add(destinationAttribute);
		return destAttributes;
	}

	/**
	 * Adds Non-Grouped Source Key. 
	 * This creates multiple Destination Attributes, each with a Value of Source Key.
	 * 
	 * @param translatorParams
	 * @param packetToOperate
	 * @param attributeMapping
	 * @param sourceParents from which the Attribute is to be fetched. 
	 * 						Effective in case of Nested Attribute Mapping 
	 * 						or Conditional Source Expression.
	 * @param destinationAttribute resultant Attribute to be created
	 * @return List of Attributes formed by processing Attribute Mapping.
	 */
	protected List<A> processNonGroupedKey(
			TranslatorParams translatorParams, P packetToOperate,
			AttributeMapping attributeMapping,
			List<A> sourceParents, A destinationAttribute) {

		NonGroupedKey sourceKey = (NonGroupedKey) attributeMapping.getSourceKey();
		List<String> sourceAttributeValues = null;
		AddValueProvider sourceValueProvider = new AddValueProvider(packetToOperate, 
				sourceParents, 
				sourceKey.getPacketExtractorMap(), 
				translatorParams);
		try {

			if(sourceKey.getElement() instanceof IdentifierExpression) {
				sourceAttributeValues = ((IdentifierExpression)sourceKey.getElement()).getStringValues(sourceValueProvider);
			} else {
				sourceAttributeValues = new ArrayList<String>();
				sourceAttributeValues.add(sourceKey.getElement().getStringValue(sourceValueProvider));
			}

		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Unable to fetch Source value, Reason: " + e.getMessage());
			}
		}

		List<A> destinationAttributes = new ArrayList<A>();
		if(Collectionz.isNullOrEmpty(sourceAttributeValues)) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Source Value not found, getting Default Value");
			}
			if(sourceKey.getDefaultValue() == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Operation not performed, Reason: Default Value not found");
				}
				return null;
			}
			//Using Default Value
			sourceAttributeValues = new ArrayList<String>();
			sourceAttributeValues.add(sourceKey.getDefaultValue());
		} 

		String destAttrId = getDestinationAttributeId(
				(NonGroupedKey) attributeMapping.getDestinationKey(), 
				new AddValueProvider(packetToOperate));
		
		//Assigning Attribute values
		for(int i = 0  ; i< sourceAttributeValues.size() ; i++) {

			String sourceValue = sourceAttributeValues.get(i);

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Adding Attribute: " + 
						destAttrId + 
						" with value: " + sourceValue);
			}
			if(sourceValue == null) {
				continue;
			}
			sourceValue = sourceKey.getValue(sourceValue);

			A clonedDestinationAttribute;
			try {
				clonedDestinationAttribute = packetDelegator.cloneAttribute(destinationAttribute);
			} catch (CloneNotSupportedException e) {
				LogManager.getLogger().trace(e);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Skipping Attribute: " + packetDelegator.getAttributeID(destinationAttribute) + 
							", Reason: " + e.getMessage());
				}
				break;
			}
			try {
				
				A destAttribute = getInnermostAttributeFromAttribute(destAttrId, clonedDestinationAttribute);
				packetDelegator.setStringValue(destAttribute, sourceValue);
			}catch(Exception e){

				LogManager.getLogger().trace(e);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Unable to process attribute: " + 
							packetDelegator.getAttributeID(clonedDestinationAttribute) + 
							", Reason: " + e.getMessage());
				}
				continue;
			}
			destinationAttributes.add(clonedDestinationAttribute);
		}
		return destinationAttributes;
	}

	protected List<A> selectConditionalParents(P packetToOperate, Key<?> key) {
		//Fetching all Parent Attributes
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Fetching Parent Attributes with Attribute-ID: " + 
					key.getParent() + " from Packet");
		}
		List<A> parentAttributes = packetDelegator.getAttributeList(packetToOperate, key.getParent());

		if(Collectionz.isNullOrEmpty(parentAttributes)) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "No Parent Attributes with Attribute-ID: " + 
						key.getParent() + " found from Packet");
			}
			return null;
		}

		List<A> selectedParentAttributes = new ArrayList<A>();

		//Filtering Parents
		for(A parentAttribute : parentAttributes) {

			AddValueProvider destinationValueProvider = new AddValueProvider(packetToOperate, parentAttribute);
			//Filtering Each parent
			if(key.getCondition() == null || key.getCondition().evaluate(destinationValueProvider)) {

				selectedParentAttributes.add(parentAttribute);
			}
		}
		return selectedParentAttributes;
	}

	protected class AddValueProvider implements ValueProvider {

		private P packet;
		private List<A> parentAttributes;
		private Map<String, KeywordValueProvider> keywordValueProviderMap;
		private TranslatorParams translatorParams;

		public AddValueProvider(P packet,
				List<A> parentAttributes,
				Map<String, KeywordValueProvider> valueExtractor,
				TranslatorParams translatorParams) {
			this(packet);
			this.parentAttributes = parentAttributes;
			this.keywordValueProviderMap = valueExtractor;
			this.translatorParams = translatorParams;
		}

		public AddValueProvider(P packet){
			this.packet = packet;
		}

		public AddValueProvider(P packetToOperate,
				A parentAttribute) {
			this(packetToOperate);
			List<A> Attributes = new ArrayList<A>();
			Attributes.add(parentAttribute);
			this.parentAttributes = Attributes;
		}

		@Override
		public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Geting value for identifier: " + identifier);
			}
			
			if(keywordValueProviderMap != null) {
				KeywordValueProvider provider = keywordValueProviderMap.get(identifier);
				if(provider != null) {
					return provider.getStringValue(translatorParams);
				}
			}
			String key = null;
			if(packetDelegator.isKeyValueSupported()){

				int index = identifier.indexOf('.'); 
				if(index != -1) {
					key = identifier.substring(index + 1);
					identifier = identifier.substring(0, index);
				}
			}
			List<A> Attributes = fetchAttributes(identifier, parentAttributes, packet, false);
			if(Collectionz.isNullOrEmpty(Attributes)) {
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			}
			if(key != null){
				return packetDelegator.getKeyValue(Attributes.get(0), key);
			}
			return packetDelegator.getStringValue(Attributes.get(0));
		}

		@Override
		public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Geting value for identifier: " + identifier);
			}
			if(keywordValueProviderMap != null) {
				KeywordValueProvider provider = keywordValueProviderMap.get(identifier);
				if(provider != null) {
					return provider.getLongValue(translatorParams);
				}
			}
			String key = null;
			if(packetDelegator.isKeyValueSupported()){

				int index = identifier.indexOf('.'); 
				if(index != -1) {
					key = identifier.substring(index + 1);
					identifier = identifier.substring(0, index);
				}
			}
			List<A> Attributes = fetchAttributes(identifier, parentAttributes, packet, false);
			if(Collectionz.isNullOrEmpty(Attributes)) {
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			}
			if(key != null){
				return Long.parseLong(packetDelegator.getKeyValue(Attributes.get(0), key));
			}
			return packetDelegator.getLongValue(Attributes.get(0));
		}

		@Override
		public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Geting value for identifier: " + identifier);
			}
			if(keywordValueProviderMap != null) {
				KeywordValueProvider provider = keywordValueProviderMap.get(identifier);
				if(provider != null) {
					return provider.getStringValues(translatorParams);
				}
			}
			String key = null;
			if(packetDelegator.isKeyValueSupported()){

				int index = identifier.indexOf('.'); 
				if(index != -1) {
					key = identifier.substring(index + 1);
					identifier = identifier.substring(0, index);
				}
			}
			List<A> attributes = fetchAttributes(identifier, parentAttributes, packet, false);
			if(Collectionz.isNullOrEmpty(attributes)) {
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			}
			List<String> stringValues = new ArrayList<String>();
			if(key != null){
				for(A Attribute : attributes){
					stringValues.add(packetDelegator.getKeyValue(Attribute, key));
				}

			} else {
				
				for(A Attribute : attributes){
					stringValues.add(packetDelegator.getStringValue(Attribute));
				}
			}
			return stringValues;
		}

		@Override
		public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Geting value for identifier: " + identifier);
			}
			if(keywordValueProviderMap != null) {
				KeywordValueProvider provider = keywordValueProviderMap.get(identifier);
				if(provider != null) {
					return provider.getLongValues(translatorParams);
				}
			}
			String key = null;
			if(packetDelegator.isKeyValueSupported()){

				int index = identifier.indexOf('.'); 
				if(index != -1) {
					key = identifier.substring(index + 1);
					identifier = identifier.substring(0, index);
				}
			}
			List<A> attributList = fetchAttributes(identifier, parentAttributes, packet, false);
			if(Collectionz.isNullOrEmpty(attributList)) {
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			}
			List<Long> longValues = new ArrayList<Long>();
			if(key != null){
				for(A Attribute : attributList){
					longValues.add(Long.parseLong(packetDelegator.getKeyValue(Attribute, key)));
				}
			} else {
				
				for(A attribute : attributList){
					longValues.add(packetDelegator.getLongValue(attribute));
				}
			}
			return longValues;
		}

		@Override
		public Object getValue(String key) {
			if (TranslatorConstants.COPY_PACKET_MAPPING_NAME.equals(key)) {
				return getPolicyName();
			}
			return null;
		}
	}

}
