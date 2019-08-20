package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.sm.client.RemoteSessionManagerClientMIB;
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
 * This communicator is used for handling session manager type of external system.
 * It has inner communication handler for maintaining the counters of Remote Session Manager client MIB.
 * 
 * @author elitecore
 *
 */
public class RemoteSessionManagerCommunicator extends RadUDPCommunicatorImpl {

	private ESIAttributeHandler esiAttributeHandler;

	public RemoteSessionManagerCommunicator(final ServerContext serverContext, DefaultExternalSystemData externalSystem) {
		super(serverContext, externalSystem);
	}

	public RemoteSessionManagerCommunicator(final ServiceContext serviceContext, DefaultExternalSystemData externalSystem) {
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
		return new RemoteSessionManagerCommHandler(getCommunicatorContext());
	}

	protected class RemoteSessionManagerCommHandler extends RadCommunicationHandlerImpl{

		public RemoteSessionManagerCommHandler(UDPCommunicatorContext communicatorContext) {
			super(communicatorContext);
		}
		
		@Override
		public void handlePreRadiusRequest(UDPRequest udpRequest, int identifier) {
			super.handlePreRadiusRequest(udpRequest, identifier);
			RemoteSessionManagerClientMIB.incrSmRequestRx(getCommunicatorContext().getName());
			
			RadUDPRequest request = (RadUDPRequest) udpRequest;
			int requestPacketType = request.getRadiusPacket().getPacketType();
			
			if(requestPacketType == RadiusConstants.ACCESS_REQUEST_MESSAGE){
				RemoteSessionManagerClientMIB.incrSmAccessRequestRx(getCommunicatorContext().getName());
			}else if(requestPacketType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
				RemoteSessionManagerClientMIB.incrSmAcctRequestRx(getCommunicatorContext().getName());
				IRadiusAttribute acctRequestType = request.getRadiusPacket().getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
				if(acctRequestType.getIntValue() == RadiusAttributeValuesConstants.START){
					RemoteSessionManagerClientMIB.incrSmAcctStartRequestRx(getCommunicatorContext().getName());
				}else if(acctRequestType.getIntValue() == RadiusAttributeValuesConstants.INTERIM_UPDATE){
					RemoteSessionManagerClientMIB.incrSmAcctUpdateRequestRx(getCommunicatorContext().getName());
				}else if(acctRequestType.getIntValue() == RadiusAttributeValuesConstants.STOP){
					RemoteSessionManagerClientMIB.incrSmAcctStopRequestRx(getCommunicatorContext().getName());
				}
			}else{
				RemoteSessionManagerClientMIB.incrSmUnknownRequestType(getCommunicatorContext().getName());
			}
		}
		
		@Override
		protected void postHandleResponse(UDPResponse udpResponse) {
			super.postHandleResponse(udpResponse);
			RemoteSessionManagerClientMIB.incrSmResponsesTx(getCommunicatorContext().getName());
			
			RadUDPResponse response = (RadUDPResponse) udpResponse;
			int responsePacketType = response.getRadiusPacket().getPacketType();

			if(responsePacketType == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
				RemoteSessionManagerClientMIB.incrSmAccessAcceptTx(getCommunicatorContext().getName());
			}else if(responsePacketType == RadiusConstants.ACCESS_REJECT_MESSAGE){
				RemoteSessionManagerClientMIB.incrSmAccessRejectTx(getCommunicatorContext().getName());
			}else if(responsePacketType == RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE){
				RemoteSessionManagerClientMIB.incrSmAcctResponseTx(getCommunicatorContext().getName());
			}
		}
		
		@Override
		public void actionOnResponseDropped(UDPResponse udpResponse) {
			RemoteSessionManagerClientMIB.incrSmRequestDropped(getCommunicatorContext().getName());
		}
		
		@Override
		public void actionOnTimeout(UDPRequest udpRequest) {
			RemoteSessionManagerClientMIB.incrSmRequestTimeout(getCommunicatorContext().getName());
		}
	}
}
