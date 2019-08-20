package com.elitecore.diameterapi.diameter.translator.operations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.delegator.PacketDelegator;
import com.elitecore.diameterapi.diameter.translator.operations.data.AttributeMapping;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.diameterapi.diameter.translator.parser.CopyPacketParser;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;


/**
 * This Operation removes a Attribute from a  Packet.
 *
 * It provides a functionality of removing Grouped and Non-Grouped Attributes. 
 *
 * This operation is applied when Destination Key Attribute arrives in Packet.
 * 
 * This operation is guarded with a Pre-Check Condition on Operation level.
 * Operation will fail if Condition Fails.
 *
 * Destination Expression is associated with Check Expression, 
 * it will remove the Attribute from a packet when Check Expression condition satisfies.
 * 
 * @author Ishani Bhatt
 *
 * @param <P> is the Packet Type to be operated
 * @param <A> is the Attribute Type to be operated
 * @param <G> is the Group Attribute Type to be operated
 */
public class RemoveOperation<P, A, G extends A> extends PacketOperation<P, A, G> {

	private static final String MODULE = "DEL-OP";

	public RemoveOperation(String policyName, OperationData operationData, PacketDelegator<P, A, G> packetDelegator) {
		super(policyName, operationData, packetDelegator);
	}

	@Override
	public void execute(TranslatorParams translatorParams) {
		@SuppressWarnings("unchecked")
		P sourcePacket = (P) translatorParams.getParam(TranslatorConstants.TO_PACKET);

		if(operationData.getCheckExpression() != null &&  
				operationData.getCheckExpression().evaluate( new RemoveValueProvider(sourcePacket) ) == false) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Check Expression failed");
			}
			return;
		}
		ValueProvider attributeValueProvider = packetDelegator.getValueProvider(sourcePacket);

		AttributeMapping attributeMapping = operationData.getAttributeMapping();

		NonGroupedKey key = (NonGroupedKey) attributeMapping.getDestinationKey();
		String destinationKey = getDestinationAttributeId(key, attributeValueProvider);

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Destination Key for removing attribute is " + destinationKey);
		}
		if(CopyPacketParser.THIS.equalsIgnoreCase(destinationKey)){
			destinationKey = key.getParent();
		}
		String[] attributeIds = packetDelegator.splitAttributeIds(destinationKey);
		
		if(CopyPacketParser.THIS.equalsIgnoreCase(attributeIds[0])){
			attributeIds[0] = key.getParent();
		}
		
		LinkedList<String> attributeQueue = new LinkedList<String>();
		for(int i = 0 ; i< attributeIds.length ; i++){
			attributeQueue.add(attributeIds[i]);
		}
		removeAttribute(sourcePacket,attributeQueue, attributeMapping.getDestinationKey().getCondition());
	}



	/**
	 * if destination key is "this.10415:1003.10415:1005" 
	 * then in Queue it will be defined as AttributeQueue [ 10415:1001,10415:1003,10415:1005] , 
	 * where 'this' refers to 10415:1001. 
	 * 
	 * if destination key is 0:296, then Queue will contain [0:296]
	 * 
	 * @param P source packet on which operation is to be performed
	 * @param attributeQueue  stores Attribute ids passed with destination key 
	 * @param condition is the Logical Expression associated with Destination Key,
	 * Attributes will not be removed if Expression evaluation fails.
	 *  
	 */
	@SuppressWarnings("unchecked")
	private void removeAttribute(P packet,
			LinkedList<String> attributeQueue, LogicalExpression condition){

		if(Collectionz.isNullOrEmpty(attributeQueue)){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Attribute can not be obtained, Skipping further Processing");
			}
			return;
		}
		String id = attributeQueue.poll();
		List<A> attributes = packetDelegator.getAttributeList(packet, id);
		if(Collectionz.isNullOrEmpty(attributes)){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Can Not remove attribute with Attribute-ID: " + id + 
						", Reason: Attribute not arrived in Packet." );
			}
			return;
		}
		for (int index = 0; index < attributes.size(); index++) {
			A attribute = attributes.get(index);
			RemoveValueProvider valueProvider = new RemoveValueProvider(
					packet, attribute);

			if (condition == null || condition.evaluate(valueProvider)) {
				if (attributeQueue.isEmpty()) {
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
						LogManager.getLogger().info(MODULE, "Removing Attribute with Attribute-ID: " + 
								packetDelegator.getAttributeID(attribute)
								+ " from Packet.");
					}
					packetDelegator.remove(packet, attribute);
					continue;
				}
				if (packetDelegator.isAttributeGrouped(attribute) == false) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE,"Attribute-Id: " + packetDelegator.getAttributeID(attribute) 
								+ " is non-grouped, Skipping Further processing");
					}
					continue;
				}
				// passed in LinkedList(attributeQueue) constructor to process
				// multiple Parent Attributes
				removeNonGroupedAttribute(new LinkedList<String>(attributeQueue),
						(G) attribute);
			}

		}
	}

	/**
	 * 
	 * @param AttributeQueue  stores Attribute ids passed with destination key 
	 * @param parentAttribute to perform operation on Grouped Attribute
	 */
	@SuppressWarnings("unchecked")
	private  void removeNonGroupedAttribute(LinkedList<String> AttributeQueue, G parentAttribute){

		String attributeId = AttributeQueue.poll();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Processing Attribute-ID: " + attributeId);
		}

		List<A> childAttributes = packetDelegator.getSubAttributesList(parentAttribute, attributeId);

		if(Collectionz.isNullOrEmpty(childAttributes)) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Unable to Remove Attribute-ID: "  + attributeId + 
						" from the parent Attribute Id " + packetDelegator.getAttributeID(parentAttribute) + 
						", Reason: Attribute not found");
			}
			return;
		}
		List<A> subAttributes = packetDelegator.getSubAttributesList(parentAttribute);

		if(AttributeQueue.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Removing Attribute: " + attributeId);
			}
			subAttributes.removeAll(childAttributes);
		} else {
			for (int i = 0; i < childAttributes.size(); i++) {
				if(packetDelegator.isAttributeGrouped(childAttributes.get(i)) == false){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "AttributeID: "+ 
								packetDelegator.getAttributeID(childAttributes.get(i))+
								" is non-grouped, Skipping Further processing" );
					}
					continue;
				}
				removeNonGroupedAttribute(new LinkedList<String>(AttributeQueue), (G) childAttributes.get(i));
			}
		}
	}

	public class RemoveValueProvider implements ValueProvider{
		private A currentAttribute;
		private P packet;

		public RemoveValueProvider(P packet, A currentAttribute){
			this(packet);
			this.currentAttribute = currentAttribute;
		}
		public RemoveValueProvider(P packet){
			this.packet = packet;
		}

		@Override
		public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {

			List<String> strAttribute = getStringValues(identifier);
			if(Collectionz.isNullOrEmpty(strAttribute) == false){
				return strAttribute.get(0).toString();
			}
			return null;
		}

		@Override
		public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
			List<Long> strAttribute = getLongValues(identifier);
			if(Collectionz.isNullOrEmpty(strAttribute) == false){
				return strAttribute.get(0).longValue();
			}else{
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			}
		}

		@Override
		public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {

			if(currentAttribute == null || identifier.startsWith(CopyPacketParser.THIS) == false ){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Getting Value for: " + identifier + " from Packet");
				}
				return packetDelegator.getValueProvider(packet).getStringValues(identifier);
			}

			String key = null;
			if(packetDelegator.isKeyValueSupported()){

				int index = identifier.indexOf('.'); 
				if(index != -1) {
					key = identifier.substring(index + 1);
					identifier = identifier.substring(0, index);
				}
			}
			List<A> attributeList = fetchAttributes(identifier);
			
			if(Collectionz.isNullOrEmpty(attributeList)){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Skipping further processing. "
							+ "Reason : No Sub-Attribute for Attribute-ID: " + identifier+ 
							" is fetched for the parent Attribute " + 
							packetDelegator.getAttributeID(currentAttribute));
				}
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			}	
			List<String> strAttribute = new ArrayList<String>();
			if(key != null){
				for(A attribute : attributeList){
					strAttribute.add(packetDelegator.getKeyValue(attribute, key));
				}

			} else {
				
				for(A attribute : attributeList){
					strAttribute.add(packetDelegator.getStringValue(attribute));
				}
			}
			return strAttribute;
		}
		
		@SuppressWarnings("unchecked")
		private List<A> fetchAttributes(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			String [] values = packetDelegator.splitAttributeIds(identifier);

			List<A> attributeList = null;

			if(values.length == 1){
				attributeList = new ArrayList<A>();
				attributeList.add(currentAttribute);
			} else {
				
				identifier = identifier.substring(5);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Getting Value for " + identifier + 
							" from Parent attribute: " + packetDelegator.getAttributeID(currentAttribute));
				}
				
				attributeList = packetDelegator.getSubAttributesList((G) currentAttribute, identifier);
			}
			return attributeList;
		}

		@Override
		public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			if(currentAttribute == null || identifier.startsWith(CopyPacketParser.THIS) == false ){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Getting Value for: " + identifier + " from Packet");
				}
				return packetDelegator.getValueProvider(packet).getLongValues(identifier);
			}

			String key = null;
			if(packetDelegator.isKeyValueSupported()){

				int index = identifier.indexOf('.'); 
				if(index != -1) {
					key = identifier.substring(index + 1);
					identifier = identifier.substring(0, index);
				}
			}
			List<A> AttributeList = fetchAttributes(identifier);
			
			if(Collectionz.isNullOrEmpty(AttributeList)){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Skipping further processing. "
							+ "Reason : No Sub-Attribute for Attribute-ID: " + identifier+ 
							" is fetched for the parent Attribute " + 
							packetDelegator.getAttributeID(currentAttribute));
				}
				throw new MissingIdentifierException("Configured identifier not found: "+identifier);
			}	
			List<Long> longValues = new ArrayList<Long>();
			if(key != null){
				for(A attribute : AttributeList){
					longValues.add(Long.parseLong(packetDelegator.getKeyValue(attribute, key)));
				}

			} else {
				
				for(A attribute : AttributeList){
					longValues.add(Long.parseLong(packetDelegator.getStringValue(attribute)));
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
