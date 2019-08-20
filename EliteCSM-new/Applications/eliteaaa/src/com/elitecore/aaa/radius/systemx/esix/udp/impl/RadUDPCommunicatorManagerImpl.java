package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.radius.accounting.client.RadiusAcctClientMIB;
import com.elitecore.aaa.mibs.radius.authentication.client.RadiusAuthClientMIB;
import com.elitecore.aaa.mibs.radius.dynauth.client.RadiusDynAuthClientMIB;
import com.elitecore.aaa.mibs.rm.charging.client.RMChargingClientMIB;
import com.elitecore.aaa.mibs.rm.ippool.client.RMIPPoolClientMIB;
import com.elitecore.aaa.mibs.sm.client.RemoteSessionManagerClientMIB;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.systemx.esix.udp.DynamicNasExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommunicatorManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPExternalSystemData;
import com.elitecore.core.systemx.esix.udp.impl.UDPCommunicatorManagerImpl;

public class RadUDPCommunicatorManagerImpl extends UDPCommunicatorManagerImpl implements RadUDPCommunicatorManager{

	AAAServerContext serverContext;
	RadiusAuthClientMIB radAuthClientMIB;
	RadiusAcctClientMIB radAcctClientMIB;
	RadiusDynAuthClientMIB radDynaAuthClientMIB;
	RMIPPoolClientMIB rmipPoolClientMIB;
	private RemoteSessionManagerClientMIB remoteSMClientMIB;
	private RMChargingClientMIB rmChargingClientMIB;
	
	public RadUDPCommunicatorManagerImpl(AAAServerContext serverContext){
		this.serverContext = serverContext;
		radAuthClientMIB = new RadiusAuthClientMIB(serverContext);
		radAcctClientMIB = new RadiusAcctClientMIB(serverContext);
		radDynaAuthClientMIB = new RadiusDynAuthClientMIB(serverContext);
		rmipPoolClientMIB = new RMIPPoolClientMIB(serverContext);
		remoteSMClientMIB = new RemoteSessionManagerClientMIB(serverContext);
		rmChargingClientMIB = new RMChargingClientMIB(serverContext);
	}

	@Override
	protected UDPCommunicator createUDPCommunicator(ServerContext serverContext, UDPExternalSystemData externalSystem) {

		switch (RadESTypeConstants.get(externalSystem.getEsiType())) {
		case CHARGING_GATEWAY:
			return new RmChargingGWCommunicator(serverContext, (DefaultExternalSystemData)externalSystem);
			
		case DYNAMIC_NAS:
			return new DynamicNASCommunicator(serverContext, (DynamicNasExternalSystemData)externalSystem);
			
		case IP_POOL_SERVER:
			return new RMIPPoolCommunicator(serverContext, (DefaultExternalSystemData)externalSystem);
			
		case NAS:
			return new NASCommunicator(serverContext, (DefaultExternalSystemData)externalSystem);
			
		case RAD_ACCT_PROXY:
			return new RadAcctCommunicator(serverContext, (DefaultExternalSystemData)externalSystem);
			
		case RAD_AUTH_PROXY:
			return new RadAuthCommunicator(serverContext, (DefaultExternalSystemData)externalSystem);
			
		case SESSION_MANAGER:
			return new RemoteSessionManagerCommunicator(serverContext, (DefaultExternalSystemData)externalSystem);
			
		default:
			return new DefaultUdpCommunicator(serverContext, (DefaultExternalSystemData)externalSystem);
		}
	}

	@Override
	protected ESIEventListener getESIEventListener(ServerContext serverContext) {
		return new RadESIEventListenerImpl(serverContext);	
	}

	@Override
	public void init() {
		radAuthClientMIB.init();
		radAcctClientMIB.init();
		radDynaAuthClientMIB.init();
		rmipPoolClientMIB.init();
		remoteSMClientMIB.init();
		rmChargingClientMIB.init();
	}
}