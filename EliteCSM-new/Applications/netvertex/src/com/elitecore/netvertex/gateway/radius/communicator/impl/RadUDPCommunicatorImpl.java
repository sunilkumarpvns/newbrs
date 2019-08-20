package com.elitecore.netvertex.gateway.radius.communicator.impl;


import java.net.InetAddress;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.systemx.esix.udp.CommunicationHandler;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPExternalSystemData;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.UDPResponseListener;
import com.elitecore.core.systemx.esix.udp.impl.UDPCommunicatorImpl;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPCommunicator;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPExternalSystem;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPRequest;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientCounters;

public class RadUDPCommunicatorImpl extends UDPCommunicatorImpl implements RadUDPCommunicator {

	private final String MODULE = "RAD-COMMUNICATOR";
	private DynaAuthClientCounters dynaAuthClientCounters;
	public RadUDPCommunicatorImpl(final ServerContext serverContext,UDPExternalSystemData externalSystem , DynaAuthClientCounters dynaAuthClientCounters) {
		super(serverContext.getTaskScheduler(), externalSystem);
		this.dynaAuthClientCounters = dynaAuthClientCounters;
	}

	@Override
	public void handleRadiusRequest(RadUDPRequest request) {
		handleUDPRequest(request);
	}

	@Override
	protected CommunicationHandler createCommunicationHandler() {
			return new RadCommunicationHandlerImpl(getCommunicatorContext() , dynaAuthClientCounters);
	}

	@Override
	public void scan() {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Scanning System " + getExternalSystem().getIPAddress().getHostAddress() + ":" + getExternalSystem().getPort() + " for aliveness");

		RadiusPacket testRequestPacket = new RadiusPacket();
		testRequestPacket.setPacketType(RadiusConstants.STATUS_SERVER_MESSAGE);
		testRequestPacket.setIdentifier(RadiusConstants.STATUS_SERVER_MESSAGE_RESERVED_ID);
		testRequestPacket.setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
		testRequestPacket.refreshPacketHeader();
		
		//Here we can use any shared secret because in case of status server message we don't need shared secret to 
		//calculate authenticator
		RadUDPRequest udpRequest = new RadUDPRequestImpl(testRequestPacket.getBytes(),"secret",new UDPResponseListener() {
			
			@Override
			public void requestTimeout(UDPRequest udpRequest,UDPCommunicator udpCommunicator) {
				
				UDPExternalSystemData externalSys = getExternalSystem();
				if(!isAlive()){
					InetAddress externalSysIp = externalSys.getIPAddress();
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "System "+ "(" + externalSysIp.getHostAddress() + ":" + externalSys.getPort() + ") is still dead");
					
					if(((RadUDPExternalSystem)getExternalSystem()).isICMPPingEnabled()){
						
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Sending ICMP Ping to System (" + externalSysIp.getHostAddress() + ":" + externalSys.getPort() + ")");
						
						try {
							if (externalSysIp.isReachable(getExternalSystem().getCommunicationTimeout())){
								if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
									LogManager.getLogger().info(MODULE, "Marking System " + "(" + externalSysIp.getHostAddress() + ":" + externalSys.getPort() + ") as Alive. Reason, System is Reachable by ICMP Ping");
								markAlive();
							}
						}catch (Exception e) {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "System "+ "(" + externalSysIp.getHostAddress() + ":" + externalSys.getPort() + ") is not Reachable by ICMP Ping");
						}
					}
				}
			}
			
			@Override
			public void requestDropped(UDPRequest udpRequest,
					UDPCommunicator udpCommunicator) {
				
			}
			

			@Override
			public void responseReceived(UDPRequest udpRequest,	UDPResponse udpResponse) {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Marking  system (" + getExternalSystem().getIPAddress().getHostAddress() + ":" + getExternalSystem().getPort() + ") as Alive. Reason, Response received  : " + udpResponse);
				markAlive();
			}
		});
		handleUDPRequest(udpRequest);
	}

	@Override
	protected void actionOnHighResponseTime(UDPRequest udpRequest,
			UDPResponse udpResponse, int endToEndResponseTime) {
		// TODO Auto-generated method stub
		
	}
}
