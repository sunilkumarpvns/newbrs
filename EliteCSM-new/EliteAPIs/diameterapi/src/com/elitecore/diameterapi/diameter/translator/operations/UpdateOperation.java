package com.elitecore.diameterapi.diameter.translator.operations;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.translator.delegator.PacketDelegator;
import com.elitecore.diameterapi.diameter.translator.operations.data.HeaderFields;
import com.elitecore.diameterapi.diameter.translator.operations.data.HeaderKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;

/**
 * Update Operation operates Destination Key when, 
 * Attribute with Attribute Id derived from Destination Key Expression,
 * is arrived in Packet.
 * 
 * @author monica.lulla
 *
 */
public class UpdateOperation<P, A, G extends A> extends BasePacketOperation<P, A, G> {

	private static final String MODULE = "UPDT-OP";


	public UpdateOperation(String policyName, OperationData operationData, PacketDelegator<P, A, G> packetDelegator) {
		super(policyName, operationData, packetDelegator);
	}

	private class UpdateDestinationKeyProcessor implements DestinationKeyProcessor<A> {

		private static final String MODULE = "UP-OP";
		private P packetToOperate;
		private List<A> selectedParents;

		public UpdateDestinationKeyProcessor(P packetToOperate) {
			this.packetToOperate = packetToOperate;
		}

		public UpdateDestinationKeyProcessor(P packetToOperate,
				List<A> destinationParents) {
			this(packetToOperate);
			this.selectedParents = destinationParents;
		}

		@Override
		public void process() {

		}
		
		@Override
		public List<A> provideParentAttributes(NonGroupedKey destinationKey) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Fetching Parent Attributes");			
			}
			return selectConditionalParents(packetToOperate, destinationKey); 
		}

		@Override
		public List<A> provideAttributes(NonGroupedKey destinationKey) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Fetching Destination Attributes");
			}
			// Get Attribute Id
			String destinationAttributeId = getDestinationAttributeId(destinationKey, new AttributeValueProvider(packetToOperate));
			// Get Attributes
			return fetchAttributes(destinationAttributeId, selectedParents, packetToOperate, true);
		}

	}

	@Override
	protected DestinationKeyProcessor<A> createDestinationKeyProcessor(
			P packetToOperate) {
		return new UpdateDestinationKeyProcessor(packetToOperate);
	}


	@Override
	protected DestinationKeyProcessor<A> createDestinationKeyProcessor(
			P packetToOperate,
			List<A> destinationParents) {
		return new UpdateDestinationKeyProcessor(packetToOperate, destinationParents);
	}


	@Override
	protected void operateHeaderKey(HeaderKey<P> headerKey, 
			P packetToOperate, 
			String sourceHeaderValue) {
		
		HeaderFields<P> headerField = headerKey.getElement();
		try{
			headerField.apply(packetToOperate, sourceHeaderValue);
		} catch(NumberFormatException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Not Updating Header-Field: " + headerField.key() + 
						", Reason: Error Parsing Value: " + sourceHeaderValue + ", " + e.getMessage());
			}
		}
	}
	
}
