package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.rm.ippool.client.RMIPPoolClientMIB;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.systemx.esix.udp.CommunicationHandler;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorContext;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.coreeap.util.constants.AttributeConstants;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * This communicator is used for handling IP pool server type of external system.
 * It has inner communication handler for maintaining the counters of RM IP-Pool client MIB.
 * 
 * @author elitecore
 *
 */
public class RMIPPoolCommunicator extends RadUDPCommunicatorImpl {

	private ESIAttributeHandler esiAttributeHandler;

	public RMIPPoolCommunicator(final ServerContext serverContext, DefaultExternalSystemData externalSystem) {
		super(serverContext, externalSystem);
	}

	public RMIPPoolCommunicator(final ServiceContext serviceContext, DefaultExternalSystemData externalSystem) {
		super(serviceContext, externalSystem);
	}
	
	@Override
	public void init() throws InitializationFailedException {
		super.init();
		initializeAttributeRemover();
	}
	
	private void initializeAttributeRemover(){
		DefaultExternalSystemData radUDPExternalSystem = (DefaultExternalSystemData)externalSystemData;
		this.esiAttributeHandler = ESIAttributeHandler.create(radUDPExternalSystem.getSupportedAttributesStr(),
				radUDPExternalSystem.getUnsupportedAttributesStr());
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		super.reInit();
		this.externalSystemData = ((AAAServerContext)getServerContext()).getServerConfiguration()
		.getRadESConfiguration().getESData(externalSystemData.getUUID()).get();
		initializeAttributeRemover();
	}
	
	@Override
	public void removeAttributes(UDPRequest udpRequest) {
		esiAttributeHandler.handleRequest((RadiusPacket)((RadUDPRequest)udpRequest).getRadiusPacket());
		((RadUDPRequest)udpRequest).getRadiusPacket().refreshPacketHeader();
	}
	
	@Override
	protected CommunicationHandler createCommunicationHandler() {
		return new RMIPPoolCommHandler(getCommunicatorContext());
	}
	

	/**
	 * Class is used for maintaining 
	 * IP-POOL-SERVICE-Client MIB Counter,
	 * @author elitecore
	 */
	
	public class RMIPPoolCommHandler extends RadCommunicationHandlerImpl{

		public RMIPPoolCommHandler(UDPCommunicatorContext communicatorContext) {
			super(communicatorContext);
		}
		
		@Override
		protected void actionOnRetransmission(UDPRequest udpRequest) {
			RMIPPoolClientMIB.incrIpAddressRequestRetransmission(getCommunicatorContext().getName());
		}

		@Override
		public void actionOnTimeout(UDPRequest udpRequest) {
			RMIPPoolClientMIB.incrIpAddressRequestTimeout(getCommunicatorContext().getName());
		}
		
		@Override
		public void handlePreRadiusRequest(UDPRequest udpRequest, int identifier) {
			
			super.handlePreRadiusRequest(udpRequest, identifier);
			RMIPPoolClientMIB.incrIpAddressTotalRequest(getCommunicatorContext().getName());

			RadUDPRequest request = (RadUDPRequest)udpRequest;
			int packetType = request.getRadiusPacket().getPacketType();
			
			if(packetType == RadiusConstants.ACCESS_REQUEST_MESSAGE){
				RMIPPoolClientMIB.incrIpAddressDiscoverRequest(getCommunicatorContext().getName());
			
			}else if (packetType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE) {
				IRadiusAttribute iRadiusAttribute = request.getRadiusPacket().getRadiusAttribute(AttributeConstants.ACCT_STATUS_TYPE_STR);

				if(iRadiusAttribute.getIntValue() == RadiusAttributeValuesConstants.START){
					RMIPPoolClientMIB.incrIpAddressAllocationRequest(getCommunicatorContext().getName());
				}else if (iRadiusAttribute.getIntValue() == RadiusAttributeValuesConstants.INTERIM_UPDATE) {
					RMIPPoolClientMIB.incrIpAddressUpdateRequest(getCommunicatorContext().getName());
				}else if(iRadiusAttribute.getIntValue() == RadiusAttributeValuesConstants.STOP){
					RMIPPoolClientMIB.incrIpAddressReleaseRequest(getCommunicatorContext().getName());
				}
			}else if (packetType == RadiusConstants.IP_ADDRESS_ALLOCATE_MESSAGE) {
				RMIPPoolClientMIB.incrIpAddressDiscoverRequest(getCommunicatorContext().getName());
			}else if (packetType == RadiusConstants.IP_UPDATE_MESSAGE) {
				RMIPPoolClientMIB.incrIpAddressUpdateRequest(getCommunicatorContext().getName());
			} else if (packetType == RadiusConstants.IP_ADDRESS_RELEASE_MESSAGE) {
				RMIPPoolClientMIB.incrIpAddressReleaseRequest(getCommunicatorContext().getName());
			}
		}
	
		@Override
		protected void postHandleResponse(UDPResponse udpResponse) {
			
			RadUDPResponse response = (RadUDPResponse)udpResponse;
			int packetType = response.getRadiusPacket().getPacketType();
			
			RMIPPoolClientMIB.incrIpAddressTotalResponse(getCommunicatorContext().getName());

			if(packetType == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
				RMIPPoolClientMIB.incrIpAddressOfferResponse(getCommunicatorContext().getName());
			}else if (packetType == RadiusConstants.ACCESS_REJECT_MESSAGE) {
				RMIPPoolClientMIB.incrIpAddressDeclineResponse(getCommunicatorContext().getName());
			}else if (packetType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE) {
				RMIPPoolClientMIB.incrIpaddressAllocationResponse(getCommunicatorContext().getName());
			}
		}
	}
}
