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
import com.elitecore.diameterapi.diameter.translator.operations.data.HeaderKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.Key;
import com.elitecore.diameterapi.diameter.translator.operations.data.KeyTypes;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * Base Packet Operation is an abstract Implementation 
 * where Main Procedure on Destination Key called as 
 * <code>DestinationKeyProcessor</code> is open to implementation.
 * 
 * Operation processes the Value of Attribute. 
 * 
 * Operation is guarded with a Pre Check Condition on Operation level.
 * Operation will fail if Condition Fails.
 * 
 * Besides, Each Source and Destination key is associated with a Check Condition,
 * Only Value of those Destination keys will be processed which satisfy the associated Condition.
 * 
 * This Destination Keys will be processed with Value with first occurrence of source key expression 
 * that satisfy the Condition associated with Source Key.
 * 
 * Operation will fail if this is Unable to find appropriate Values.
 * 
 * @author monica.lulla
 * 
 * @see DestinationKeyProcessor
 *
 */
public abstract class BasePacketOperation<P, A, G extends A> extends PacketOperation<P, A, G> {

	private static final String MODULE = "BASE-PKT-OP";

	public BasePacketOperation(String policyName, OperationData operationData, PacketDelegator<P, A, G> packetDelegator) {
		super(policyName, operationData, packetDelegator);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(TranslatorParams translatorParams) {

		P packetToOperate = (P) translatorParams.getParam(TranslatorConstants.TO_PACKET);

		//Checking for Base Condition
		if(operationData.getCheckExpression() != null && 
				operationData.getCheckExpression().evaluate( new AttributeValueProvider(packetToOperate) ) == false) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Check Expression failed");
			}
			return;
		}
		AttributeMapping attributeMapping = operationData.getAttributeMapping();

		if(attributeMapping.getDestinationKey().getKeyType() == KeyTypes.HEADER){
			operateHeaderKey((HeaderKey<P>) attributeMapping.getDestinationKey(), packetToOperate, obtainSourceValue(
					translatorParams, packetToOperate, attributeMapping, null));
			return;
		}
		
		//Get Parented Destination Attributes
		List<A> selectedDestinationParents  = null;
		DestinationKeyProcessor<A> attributeProcessor = createDestinationKeyProcessor(packetToOperate);
		NonGroupedKey key = (NonGroupedKey) attributeMapping.getDestinationKey();
		if(key.getParent() != null) {

			selectedDestinationParents = attributeProcessor.provideParentAttributes(key);
			if(Collectionz.isNullOrEmpty(selectedDestinationParents)) {

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Unable to fetch Attributes for:  " + 
							attributeMapping.getDestinationKey().getParent());
				}
				return;
			}
		}
		//Get Parented Source Attributes
		List<A> selectedSourceParents = null;
		if(attributeMapping.getSourceKey().getParent() != null) { 

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Fetching Source Parent Attributes");
			}
			selectedSourceParents = selectConditionalParents(packetToOperate, 
					attributeMapping.getSourceKey());

			if(Collectionz.isNullOrEmpty(selectedSourceParents)) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Unable to fetch Attributes for: " + 
							attributeMapping.getSourceKey().getParent());
				}
				return;
			}
			selectedSourceParents = selectedSourceParents.subList(0, 1);
		}

		operateAttributeMapping(translatorParams, packetToOperate,
				attributeMapping, selectedSourceParents, selectedDestinationParents);
		attributeProcessor.process();
	}

	private void operateAttributeMapping(TranslatorParams translatorParams,
			P packetToOperate, AttributeMapping attributeMapping,
			List<A> sourceParents, List<A> destinationParents) {

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Processing Attribute Mapping");
		}
		// Select Main Attribute on which Updation is to be performed
		DestinationKeyProcessor<A> childProcessor = createDestinationKeyProcessor(packetToOperate, destinationParents);
		List<A> destAttributes =  childProcessor.provideAttributes((NonGroupedKey) attributeMapping.getDestinationKey());

		if(Collectionz.isNullOrEmpty(destAttributes)) {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "No destination Attributes found");
			}
			return;
		}
		// if Destination Key is of Type AttributeGrouped and and Source Key is Grouped
		if(attributeMapping.getSourceKey().getKeyType() == KeyTypes.GROUPED && 
				packetDelegator.isAttributeGrouped(destAttributes.get(0))) {

			operateGroupedSourceKey(translatorParams, packetToOperate,
					attributeMapping, sourceParents, destAttributes);
		
		// if Destination Key is of Type AttributeGrouped and and Source Key is Grouped
		} else if(attributeMapping.getSourceKey().getKeyType() == KeyTypes.NONGROUPED) {

			operateNonGroupedSourceKey(translatorParams, packetToOperate,
					attributeMapping, sourceParents, destAttributes);
		
		//In-Compatible Types
		} else {
			String destinationType = packetDelegator.isAttributeGrouped(destAttributes.get(0)) ? "GROUPED" : "NONGROUPED";

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Operation not performed, Reason: Incompatible Source(Grouped) and Destination(" 
							+destinationType+") Attribute Type");
			}
		}
		childProcessor.process();
	}

	protected abstract void operateHeaderKey(HeaderKey<P> headerKey, P packet, String sourceHeaderValue);

	private void operateGroupedSourceKey(TranslatorParams translatorParams,
			P packetToOperate, AttributeMapping attributeMapping,
			List<A> sourceParents,
			List<A> destAttributes) {
		
		GroupedKey sourceKey =  (GroupedKey) attributeMapping.getSourceKey();

		for(AttributeMapping mapping : sourceKey.getElement()) {

			List<A> selectedSourceParents = new ArrayList<A>();

			if(mapping.getSourceKey().getParent() != null) { 

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Fetching Source Parent Attributes");
				}
				List<A> sourceParentAttributes = selectConditionalParents(packetToOperate, 
						mapping.getSourceKey());
				if(Collectionz.isNullOrEmpty(sourceParentAttributes)) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Unable to fetch Attributes for:  " + 
								mapping.getSourceKey());
					}
					continue;
				}
				//Considering only one Source Attribute of all
				selectedSourceParents.add(sourceParentAttributes.get(0));
			} 
			if(Collectionz.isNullOrEmpty(sourceParents) == false) {
				selectedSourceParents.addAll(sourceParents);
			}
			operateAttributeMapping(translatorParams, 
					packetToOperate, mapping, 
					selectedSourceParents, destAttributes);
		}
	}

	protected void operateNonGroupedSourceKey(TranslatorParams translatorParams,
			P packetToOperate, AttributeMapping attributeMapping,
			List<A> sourceParents,
			List<A> destAttributes) {

		String sourceAttributeValue = obtainSourceValue(translatorParams,
				packetToOperate, attributeMapping, sourceParents);
		
		if(sourceAttributeValue == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Operation not performed, Reason: Source Value not found");
			}
			return;
		} 
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Updating Destination Attributes with Attribute-ID: " + 
					packetDelegator.getAttributeID(destAttributes.get(0))+ 
					" with Value: " + sourceAttributeValue);
		}
		//Assigning Attribute values
		for(int i = 0  ; i< destAttributes.size() ; i++) {
			try {
				packetDelegator.setStringValue(destAttributes.get(i), sourceAttributeValue);
			}catch(Exception e){

				LogManager.getLogger().trace(e);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Unable to process attribute: " + 
							packetDelegator.getAttributeID(destAttributes.get(i)) + 
							", Reason: " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Fetch Source Value from Source Expression
	 * If No Value --> Return Default Value
	 * Else Apply Value Mapping --> return Mapped Value 
	 */
	private String obtainSourceValue(TranslatorParams translatorParams,
			P packetToOperate, AttributeMapping attributeMapping,
			List<A> sourceParents) {

		NonGroupedKey sourceKey = (NonGroupedKey) attributeMapping.getSourceKey();

		String sourceAttributeValue = null;
		AttributeValueProvider sourceValueProvider = new AttributeValueProvider(packetToOperate, 
				sourceParents, 
				sourceKey.getPacketExtractorMap(), 
				translatorParams);
		try {
			sourceAttributeValue = sourceKey.getElement().getStringValue(sourceValueProvider);
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Unable to fetch Source value, Reason: " + e.getMessage());
			}
		}
		//Default Value and Value Mappings 
		if(sourceAttributeValue == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Source Value not found, getting Default Value");
			}
			return sourceKey.getDefaultValue();
		} 
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Applying Value Mapping for: " + sourceAttributeValue);
		}
		return sourceKey.getValue(sourceAttributeValue);
	}

	protected abstract DestinationKeyProcessor<A> createDestinationKeyProcessor(P packetToOperate);

	protected abstract DestinationKeyProcessor<A> createDestinationKeyProcessor(
			P packetToOperate,
			List<A> destinationParents);

	protected final List<A> selectConditionalParents(P packetToOperate, Key<?> key) {
		//Fetching all Parent AttributeS
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

			AttributeValueProvider destinationValueProvider = new AttributeValueProvider(packetToOperate, parentAttribute);
			//Filtering Each parent
			if(key.getCondition() == null || key.getCondition().evaluate(destinationValueProvider)) {

				selectedParentAttributes.add(parentAttribute);
			}
		}
		return selectedParentAttributes;
	}

	protected final class AttributeValueProvider implements ValueProvider{

		private P packet;
		private List<A> parentAttributes;
		private Map<String, KeywordValueProvider> keywordValueProvider;
		private TranslatorParams translatorParams;

		public AttributeValueProvider(P packet,
				List<A> parentAttributes,
				Map<String, KeywordValueProvider> valueExtractor,
				TranslatorParams translatorParams) {
			this.packet = packet;
			this.parentAttributes = parentAttributes;
			this.keywordValueProvider = valueExtractor;
			this.translatorParams = translatorParams;
		}

		public AttributeValueProvider(P packet){
			this.packet = packet;
		}

		public AttributeValueProvider(P packetToOperate,
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
			if(keywordValueProvider != null) {
				KeywordValueProvider provider = keywordValueProvider.get(identifier);
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
			if(keywordValueProvider != null) {
				KeywordValueProvider provider = keywordValueProvider.get(identifier);
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
			if(key != null) {
				return Long.parseLong(packetDelegator.getKeyValue(Attributes.get(0), key));
			}
			return packetDelegator.getLongValue(Attributes.get(0));
		}

		@Override
		public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Geting value for identifier: " + identifier);
			}
			if(keywordValueProvider != null) {
				KeywordValueProvider provider = keywordValueProvider.get(identifier);
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
			if(key != null) {
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
			if(keywordValueProvider != null) {
				KeywordValueProvider provider = keywordValueProvider.get(identifier);
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
			List<A> attributes = fetchAttributes(identifier, parentAttributes, packet, false);
			if(Collectionz.isNullOrEmpty(attributes)) {
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			}
			List<Long> longValues = new ArrayList<Long>();
			if(key != null) {
				for(A attribute : attributes){
					longValues.add(Long.parseLong(packetDelegator.getKeyValue(attribute, key)));
				}
			} else {
				for(A attribute : attributes){
					longValues.add(packetDelegator.getLongValue(attribute));
				}
			}
			return longValues;
		}

		@Override
		public Object getValue(String key) {
			return null;
		}
	}

}
