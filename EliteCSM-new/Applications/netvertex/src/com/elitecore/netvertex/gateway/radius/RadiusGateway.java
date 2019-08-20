package com.elitecore.netvertex.gateway.radius;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.gateway.radius.communicator.*;
import com.elitecore.netvertex.gateway.radius.communicator.impl.RadUDPCommunicatorGroupImpl;
import com.elitecore.netvertex.gateway.radius.communicator.impl.RadUDPCommunicatorImpl;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;
import com.elitecore.netvertex.gateway.radius.snmp.dynaauth.DynaAuthClientCounters;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Interface for Radius Gateway
 * 
 * @author Subhash Punani
 *
 */
public class RadiusGateway {
	
	private static final String MODULE = "RAD-GTW";
	
	private NetVertexServerContext serverContext;
	private RadiusGatewayConfiguration configuration;
	private RadUDPCommunicatorGroup radUDPCommGrp;
	private RadUDPCommunicatorImpl udpCommunicator;
	
	private DynaAuthClientCounters dynaAuthClientCounters;
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	public RadiusGateway(NetVertexServerContext serverContext, RadiusGatewayConfiguration configuration,
			DynaAuthClientCounters dynaAuthClientCounters, ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
		this.serverContext = serverContext;
		this.configuration = configuration;
		this.dynaAuthClientCounters = dynaAuthClientCounters; 
		this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
	}
	
	public void init()throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initializing Radius Gateway: " + configuration.getIPAddress() + ":" + configuration.getPort());
		
		try {
			String connectionURL = configuration.getIPAddress()+":"+configuration.getPort();
			RadUDPExternalSystem udpExternalSystem = new RadUDPExternalSystem(configuration.getGatewayId(), connectionURL, configuration.getMaxRequestTimeout(), configuration.getTimeout(), configuration.getMinimumLocalPort(), configuration.getSharedSecret(), configuration.getRetryCount(),configuration.getStatusCheckDuration(), configuration.getConnectionURL(),configuration.isICMPPingEnabled());
			udpExternalSystem.setIPAddress(InetAddress.getByName(configuration.getIPAddress()));
			udpExternalSystem.setPort(configuration.getPort());
			
			udpCommunicator = new RadUDPCommunicatorImpl(serverContext, udpExternalSystem ,  dynaAuthClientCounters);
			udpCommunicator.init();
			
			udpCommunicator.addESIEventListener(new ESIEventListener<ESCommunicator>() {
				
				@Override
				public void alive(ESCommunicator esCommunicator) {
					serverContext.generateSystemAlert(AlertSeverity.INFO,Alerts.RADIUS_ALIVE, MODULE,((UDPCommunicator)esCommunicator).getCommunicatorContext().getIPAddress()+":"+ ((UDPCommunicator)esCommunicator).getCommunicatorContext().getPort() + "Marked Alive.");
				}
				
				@Override
				public void dead(ESCommunicator esCommunicator) {
					serverContext.generateSystemAlert(AlertSeverity.CRITICAL,Alerts.RADIUS_DEAD, MODULE, ((UDPCommunicator)esCommunicator).getCommunicatorContext().getIPAddress()+":"+ ((UDPCommunicator)esCommunicator).getCommunicatorContext().getPort() + "Marked Dead.");
				}
				
			});
			radUDPCommGrp = new RadUDPCommunicatorGroupImpl(serverContext);
			radUDPCommGrp.addCommunicator(udpCommunicator);
			
		} catch (UnknownHostException e) {
			throw new InitializationFailedException("Error in initializing Radius Gateway: " + configuration.getIPAddress() + ":" + configuration.getPort() + ", reason : UnknownhostException " + e.getMessage(),e);
		}
		LogManager.getLogger().info(MODULE, "Radius Gateway: " + configuration.getIPAddress() + ":" + configuration.getPort() +" initialization completed");
	}
	
	public void sendRequest(IRadiusPacket radiusPacket) {
		
		long delay = serverContext.getServerConfiguration().getMiscellaneousParameterConfiguration().getCOADelay();
		if (radiusPacket.getPacketType() == RadiusConstants.COA_REQUEST_MESSAGE && delay > 0) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Delaying COA with " + delay + "sec");
			}
			scheduledThreadPoolExecutor.schedule(new RequestDelayTask(radiusPacket), delay, TimeUnit.SECONDS);
		} else {
			submitRequest(radiusPacket);
		}
	}

	private void submitRequest(IRadiusPacket radiusPacket) {
		radUDPCommGrp.handleRequest(radiusPacket, new RadResponseListenerImpl());
	}
	
	private static ILogger getLogger() {
		return LogManager.getLogger();
	}
	
	public RadiusGatewayConfiguration getConfiguration() {
		return configuration;
	}
	
	public String getIPAddress() {
		return configuration.getIPAddress();
	}
	
	public String getSharedSecret() {
		return configuration.getSharedSecret();
	}
	
	public boolean isAccountingResponseEnable() {
		return configuration.isAccountingResponseEnable();
	}
	
	public void stop(){
		this.udpCommunicator.shutdown();
	}
	
	public boolean isAlive() {
		return udpCommunicator.isAlive();
	}
	
	private class RequestDelayTask implements Runnable{

		private IRadiusPacket packet;
		
		public RequestDelayTask(IRadiusPacket packet) {
			this.packet = packet;
		}
		
		@Override
		public void run() {
			submitRequest(packet);
		}
	
	}

	private static class RadResponseListenerImpl implements RadResponseListner{
		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse) {

			if(radUDPResponse.getRadiusPacket().getPacketType() == RadiusConstants.COA_NAK_MESSAGE){
				if(getLogger().isLogLevel(LogLevel.WARN))
					getLogger().warn(MODULE, "RADIUS response received " + radUDPResponse);
			}else{
				if(getLogger().isLogLevel(LogLevel.INFO))
					getLogger().info(MODULE, "RADIUS response received " + radUDPResponse);
			}
		}

		@Override
		public void requestTimeout(UDPRequest radUDPRequest) {

			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "RADIUS request Timeout " + radUDPRequest);
		}

		@Override
		public void requestDropped(UDPRequest radUDPRequest) {

			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "RADIUS request has been droped " + radUDPRequest);
		}

		@Override
		public RadServiceResponse getServiceResponse() {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Service response");
			return null;
		}

		@Override
		public RadServiceRequest getServiceRequest() {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Service request");
			return null;
		}
	}


}
