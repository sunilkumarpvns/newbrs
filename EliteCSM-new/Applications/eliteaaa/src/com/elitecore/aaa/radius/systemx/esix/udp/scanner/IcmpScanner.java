package com.elitecore.aaa.radius.systemx.esix.udp.scanner;

import java.io.IOException;
import java.net.InetAddress;

import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommunicatorImpl;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class IcmpScanner implements StatusScanner {

	private static final String MODULE = "ICMP-SCNR";
	private final RadUDPCommunicatorImpl udpCommunicator;

	public IcmpScanner(RadUDPCommunicatorImpl udpCommunicator) {
		this.udpCommunicator = udpCommunicator;
	}

	@Override
	public void scan() {
		InetAddress IPAddress = udpCommunicator.getCommunicatorContext().getExternalSystem().getIPAddress();
		try {
			if(IPAddress.isReachable(udpCommunicator.getCommunicatorContext().getExternalSystem().getCommunicationTimeout())){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "System: " + udpCommunicator.getCommunicatorContext().getExternalSystem().getName() + 
							"[" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + 
							udpCommunicator.getCommunicatorContext().getExternalSystem().getPort() + 
							"] is Reachable by ICMP Ping. So marking as ALIVE.");
				udpCommunicator.markAlive();
				return;
			}
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "System "+ udpCommunicator.getCommunicatorContext().getExternalSystem().getName() + 
						"[" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + 
						udpCommunicator.getCommunicatorContext().getExternalSystem().getPort() + 
						"] is not reachable by ICMP, marking as DEAD.");
			udpCommunicator.markDead();
			return;
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "System "+ 
					udpCommunicator.getCommunicatorContext().getExternalSystem().getName() + 
					"[" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" +
					udpCommunicator.getCommunicatorContext().getExternalSystem().getPort() + 
					"] is not reachable by ICMP, marking as DEAD.");
		udpCommunicator.markDead();
	} 

	@Override
	public void init() {

	}

}
