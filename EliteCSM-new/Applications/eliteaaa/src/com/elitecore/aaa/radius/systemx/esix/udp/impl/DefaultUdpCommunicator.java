package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.systemx.esix.udp.CommunicationHandler;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

/**
 * <p>This is the communicator which do not required the specific communication handler
 * because there is no need to update the counters of any particular MIB.</p>
 * 
 * <p>This communicator is used for prepaid server type of external system.</p>
 * 
 * @author Kuldeep Panchal
 *
 */
public class DefaultUdpCommunicator extends RadUDPCommunicatorImpl {

	private ESIAttributeHandler esiAttributeHandler;

	public DefaultUdpCommunicator(final ServerContext serverContext, DefaultExternalSystemData externalSystem) {
		super(serverContext, externalSystem);
	}

	public DefaultUdpCommunicator(final ServiceContext serviceContext, DefaultExternalSystemData externalSystem) {
		super(serviceContext, externalSystem);
	}
	
	@Override
	public void init() throws InitializationFailedException {
		super.init();
		initializeAttributeRemover();
	}
	
	private void initializeAttributeRemover(){
		DefaultExternalSystemData radUDPExternalSystemData = (DefaultExternalSystemData)externalSystemData;
		this.esiAttributeHandler = ESIAttributeHandler.create(radUDPExternalSystemData.getSupportedAttributesStr(),
				radUDPExternalSystemData.getUnsupportedAttributesStr());
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
		return new RadCommunicationHandlerImpl(getCommunicatorContext());
	}
}
