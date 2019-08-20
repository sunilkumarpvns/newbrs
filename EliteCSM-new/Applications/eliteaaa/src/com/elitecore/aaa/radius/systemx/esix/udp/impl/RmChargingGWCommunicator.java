package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.rm.charging.client.RMChargingClientMIB;
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
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * This communicator is used for handling charging gateway type of external system.
 * It has inner communication handler for maintaining the counters of RM Charging client MIB.
 * 
 * @author elitecore
 *
 */
public class RmChargingGWCommunicator extends RadUDPCommunicatorImpl {
	
	private ESIAttributeHandler esiAttributeHandler;

	public RmChargingGWCommunicator(final ServerContext serverContext, DefaultExternalSystemData externalSystem) {
		super(serverContext, externalSystem);
	}

	public RmChargingGWCommunicator(final ServiceContext serviceContext, DefaultExternalSystemData externalSystem) {
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
		return new RmChargingGWCommHandler(getCommunicatorContext());
	}
	
	protected class RmChargingGWCommHandler extends RadCommunicationHandlerImpl{

		public RmChargingGWCommHandler(UDPCommunicatorContext communicatorContext) {
			super(communicatorContext);
		}
		
		@Override
		public void handlePreRadiusRequest(UDPRequest udpRequest, int identifier) {
			super.handlePreRadiusRequest(udpRequest, identifier);
			RMChargingClientMIB.incrRmChargingRequest(getCommunicatorContext().getName());
			
			RadUDPRequest request = (RadUDPRequest) udpRequest;
			int requestPacketType = request.getRadiusPacket().getPacketType();
			
			if(requestPacketType == RadiusConstants.ACCESS_REQUEST_MESSAGE){
				RMChargingClientMIB.incrRmChargingAccessRequest(getCommunicatorContext().getName());
			}else if(requestPacketType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
				RMChargingClientMIB.incrRmChargingAcctRequest(getCommunicatorContext().getName());
				IRadiusAttribute acctRequestType = request.getRadiusPacket().getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
				if(acctRequestType.getIntValue() == RadiusAttributeValuesConstants.START){
					RMChargingClientMIB.incrRmChargingAcctStartRequest(getCommunicatorContext().getName());
				}else if(acctRequestType.getIntValue() == RadiusAttributeValuesConstants.INTERIM_UPDATE){
					RMChargingClientMIB.incrRmChargingAcctUpdateRequest(getCommunicatorContext().getName());
				}else if(acctRequestType.getIntValue() == RadiusAttributeValuesConstants.STOP){
					RMChargingClientMIB.incrRmChargingAcctStopRequest(getCommunicatorContext().getName());
				}
			}else{
				RMChargingClientMIB.incrRmChargingUnknownRequestType(getCommunicatorContext().getName());
			}
		}
		
		@Override
		protected void postHandleResponse(UDPResponse udpResponse) {
			RMChargingClientMIB.incrRmChargingResponses(getCommunicatorContext().getName());
			
			RadUDPResponse response = (RadUDPResponse) udpResponse;
			int responsePacketType = response.getRadiusPacket().getPacketType();

			if(responsePacketType == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
				RMChargingClientMIB.incrRmChargingAccessAccept(getCommunicatorContext().getName());
			}else if(responsePacketType == RadiusConstants.ACCESS_REJECT_MESSAGE){
				RMChargingClientMIB.incrRmChargingAccessReject(getCommunicatorContext().getName());
			}else if(responsePacketType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE){
				RMChargingClientMIB.incrRmChargingAcctResponse(getCommunicatorContext().getName());
			}
		}
		
		@Override
		public void actionOnResponseDropped(UDPResponse udpResponse) {
			RMChargingClientMIB.incrRmChargingRequestDropped(getCommunicatorContext().getName());
		}
		
		@Override
		public void actionOnTimeout(UDPRequest udpRequest) {
			RMChargingClientMIB.incrRmChargingRequestTimeout(getCommunicatorContext().getName());
		}
		
		@Override
		protected void actionOnRetransmission(UDPRequest udpRequest) {
			RMChargingClientMIB.incrRmChargingRequestRetransmission(getCommunicatorContext().getName());
		}
	}
}
