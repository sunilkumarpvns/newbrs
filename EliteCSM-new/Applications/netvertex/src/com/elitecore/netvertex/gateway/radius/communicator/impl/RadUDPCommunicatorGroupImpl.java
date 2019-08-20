package com.elitecore.netvertex.gateway.radius.communicator.impl;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.UDPResponseListener;
import com.elitecore.core.systemx.esix.udp.impl.UDPCommunicatorGroupImpl;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.netvertex.gateway.radius.communicator.RadResponseListner;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPCommunicator;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPCommunicatorGroup;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPRequest;
import com.elitecore.netvertex.gateway.radius.communicator.RadUDPResponse;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;

public class RadUDPCommunicatorGroupImpl extends UDPCommunicatorGroupImpl implements RadUDPCommunicatorGroup {

	private final String MODULE = "RAD-UDP-COMM-GRP";
	public RadUDPCommunicatorGroupImpl(ServerContext serverContext) {
		super();
		
	}
	
	@Override
	public void handleRequest(IRadiusPacket request,RadResponseListner responseListner) {
		RadUDPRequestImpl udpRequest = new RadUDPRequestImpl(request.getBytes(), "secret", new PrimaryResponseListnerImpl(responseListner));
		handleRequest(udpRequest, responseListner);
	}
	
	@Override
	public void handleRequest(RadServiceRequest request, RadServiceResponse response,RadResponseListner responseListner) {
		RadUDPRequestImpl udpRequest = new RadUDPRequestImpl(request.getRequestBytes(), "secret", new PrimaryResponseListnerImpl(responseListner));
		handleRequest(udpRequest, responseListner);
	}

	/**
	 * This method handle the Radius request from using communicator.
	 * @param udpRequest
	 * @param responseListner
	 */
	private void handleRequest(RadUDPRequest udpRequest,RadResponseListner responseListner){
		RadUDPCommunicator communicator = (RadUDPCommunicator) getCommunicator();
		if(communicator == null){
			LogManager.getLogger().error(MODULE, "No Alive Communicator found in group. Dropping request");
			responseListner.requestDropped(udpRequest);
			return;
		}
		
		communicator.handleRadiusRequest(udpRequest);		
	}
	
	/**
	 * The UDPResponseListenerImpl handle the Radius responseReceived event. 
	 * @author Harsh Patel
	 *
	 */
	private abstract class UDPResponseListnerImpl implements UDPResponseListener{
		protected RadResponseListner responseListner;
		public UDPResponseListnerImpl(RadResponseListner responseListner) {
			this.responseListner = responseListner;
		}
		
		/**
		 * Thid method handle the Radius's responseRecieved event.
		 * @param <code>UDPResponse</code>
		 */
		@Override
		public void responseReceived(UDPRequest udpRequest, UDPResponse udpResponse) {
			LogManager.getLogger().info(MODULE, "Response Received");
			responseListner.responseReceived((RadUDPRequest)udpRequest,(RadUDPResponse)udpResponse);
		}
	}
	
	/**
	 * The Primary Response Listener handle the Radius response event.
	 * @author Harsh
	 *
	 */
	private class PrimaryResponseListnerImpl extends UDPResponseListnerImpl{

		public PrimaryResponseListnerImpl(RadResponseListner responseListner) {
			super(responseListner);
		}

		/**
		 * This method handle the request timeout event if secondary external system communicator is found is send the request to it.
		 * @param <code>UDPRequest</code>
		 * @param <code>UDPCommunicator</code>  
		 */
		@Override
		public void requestTimeout(UDPRequest udpRequest,
				UDPCommunicator udpCommunicator) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Timeout response received from server: " + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + udpCommunicator.getCommunicatorContext().getPort());
			RadUDPCommunicator secondaryCommunicator = (RadUDPCommunicator) getSecondaryCommunicator(udpCommunicator);
			if(secondaryCommunicator == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "No other target system found in group to resend request");
				responseListner.requestTimeout(udpRequest);
				return;
			}
			RadUDPRequest radUDPRequest = (RadUDPRequest) udpRequest;
			RadUDPRequest udpRequestForSecondarySever = new RadUDPRequestImpl(udpRequest.getBytes(), radUDPRequest.getSharedSecret(), new SecondaryResponseListnerImpl(responseListner));
			secondaryCommunicator.handleRadiusRequest(udpRequestForSecondarySever);
		}
		
		/**
		 * This method handle the request dropped event if secondary external system communicator is found is send the request to it.
		 * @param <code>UDPRequest</code>
		 * @param <code>UDPCommunicator</code> 
		 */
		@Override
		public void requestDropped(UDPRequest udpRequest,
				UDPCommunicator udpCommunicator) {
			RadUDPCommunicator secondaryCommunicator = (RadUDPCommunicator) getSecondaryCommunicator(udpCommunicator);
			if(secondaryCommunicator == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "No other target system found in group to resend request");
				responseListner.requestDropped(udpRequest);
				return;
			}
			RadUDPRequest radUDPRequest = (RadUDPRequest) udpRequest;
			RadUDPRequest udpRequestForSecondarySever = new RadUDPRequestImpl(udpRequest.getBytes(), radUDPRequest.getSharedSecret(), new SecondaryResponseListnerImpl(responseListner));
			secondaryCommunicator.handleRadiusRequest(udpRequestForSecondarySever);
		}
	}
	
	/**
	 * This class provide the secondary lister whenever response from external system to primary listener is dropped 
	 * or timeout. 
	 * @author Harsh Patel
	 *
	 */
	private class SecondaryResponseListnerImpl extends UDPResponseListnerImpl{
		public SecondaryResponseListnerImpl(RadResponseListner responseListner) {
			super(responseListner);
		}

		@Override
		public void requestTimeout(UDPRequest udpRequest,
				UDPCommunicator udpCommunicator) {
			LogManager.getLogger().debug(MODULE, "Timeout response received from secondary server.");
			responseListner.requestTimeout(udpRequest);
		}
		
		@Override
		public void requestDropped(UDPRequest udpRequest,
				UDPCommunicator udpCommunicator) {
			responseListner.requestDropped(udpRequest);
		}
	}
}
