package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommGroup;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommunicator;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.UDPResponseListener;
import com.elitecore.core.systemx.esix.udp.impl.UDPCommunicatorGroupImpl;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadUDPCommGroupImpl extends UDPCommunicatorGroupImpl implements
		RadUDPCommGroup {
	private static final String MODULE = "RAD-COMM-GRP";
	private ServerContext serverContext;

	public RadUDPCommGroupImpl(ServerContext serverContext) {
		this.serverContext = serverContext;
	}

	public RadUDPCommGroupImpl(ServiceContext serviceContext,String translationMapping) {
		this.serverContext = serviceContext.getServerContext();
	}

	@Override
	public void handleRequest(byte[] requestBytes, String secret, RadResponseListener responseListner, ISession session) {
		RadUDPRequestImpl udpRequest = new RadUDPRequestImpl(requestBytes, secret, 
				new PrimaryResponseListnerImpl(responseListner));

		handleRequest(udpRequest, responseListner);
	}
	
	private void handleRequest(RadUDPRequest udpRequest,RadResponseListener responseListner){
		RadUDPCommunicator communicator = (RadUDPCommunicator) getCommunicator();
		if(communicator == null){
			LogManager.getLogger().error(MODULE, "No Alive Communicator found in group. Dropping request");
			responseListner.requestDropped(udpRequest);
			return;
		}
		
		communicator.handleRadiusRequest(udpRequest);		
	}
	
	private abstract class UDPResponseListnerImpl implements UDPResponseListener{
		protected RadResponseListener responseListner;
		public UDPResponseListnerImpl(RadResponseListener responseListner) {
			this.responseListner = responseListner;
		}
		
		@Override
		public void responseReceived(UDPRequest udpRequest, UDPResponse udpResponse){		
			RadUDPResponse radUDPResponse = (RadUDPResponse)udpResponse;
			IRadiusAttribute satisfiedESI = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_SATISFIED_ESI);
			if(satisfiedESI != null){
				satisfiedESI.setStringValue(udpResponse.getCommunicatorName());
				radUDPResponse.getRadiusPacket().addInfoAttribute(satisfiedESI);
			}

			responseListner.responseReceived((RadUDPRequest)udpRequest, radUDPResponse, HazelcastRadiusSession.RAD_NO_SESSION);
		}
	}
	
	private class PrimaryResponseListnerImpl extends UDPResponseListnerImpl{

		public PrimaryResponseListnerImpl(RadResponseListener responseListner) {
			super(responseListner);
		}

		@Override
		public void requestTimeout(UDPRequest udpRequest,
				UDPCommunicator udpCommunicator) {
			
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Timeout response received from server: " + udpCommunicator.getName() 
				+ " [" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + udpCommunicator.getCommunicatorContext().getPort() + "] "
				+ "for request id: " + udpRequest.getIdentifier());
			}
			
			getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.RADIUS_REQUEST_TIMEOUT, 
					MODULE, "Timeout response received from server: " 
							+ udpCommunicator.getCommunicatorContext().getIPAddress() + ":" 
							+ udpCommunicator.getCommunicatorContext().getPort(),0,
							udpCommunicator.getCommunicatorContext().getIPAddress() + ":" 
									+ udpCommunicator.getCommunicatorContext().getPort());
			
			RadUDPCommunicator secondaryCommunicator = (RadUDPCommunicator) getSecondaryCommunicator(udpCommunicator);
			if(secondaryCommunicator == null){
				
				if (hasMultipleCommunicatorsConfigured()) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "No other target system found in group to resend request "
								+ "for request id: " + udpRequest.getIdentifier());
					}
				}
				
				responseListner.requestTimeout((RadUDPRequest)udpRequest);
				return;
			}
			RadUDPRequest radUDPRequest = (RadUDPRequest) udpRequest;

			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,"Removing proxy state attribute from request which has been added by : " + udpCommunicator.getName());
			}
			/** remove proxy state AVP from request which has been added by primary communicator ESI */
			removeProxyStateAvp(radUDPRequest);
			
			RadUDPRequest udpRequestForSecondarySever = new RadUDPRequestImpl(udpRequest.getBytes(), radUDPRequest.getSharedSecret(), new SecondaryResponseListnerImpl(responseListner));
			secondaryCommunicator.handleRadiusRequest(udpRequestForSecondarySever);
		}

		/**
		 * This removes proxy-state AVP from request.
		 * While forwarding request packet to primary ESI AAA successfully adds proxy-state attribute, if fail-over occurs
		 * then before forwarding request to secondary ESI AAA need to remove proxy-state attribute which has been added by 
		 * primary ESI.
		 */
		private void removeProxyStateAvp(RadUDPRequest radUDPRequest) {
			Collection<IRadiusAttribute> receivedProxyStates = radUDPRequest.getRadiusPacket().getRadiusAttributes(RadiusAttributeConstants.PROXY_STATE);

			if (Collectionz.isNullOrEmpty(receivedProxyStates) == false) {

				IRadiusAttribute primaryESIsProxyState = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.PROXY_STATE);
				
				if (primaryESIsProxyState != null) {
					
					primaryESIsProxyState.setStringValue(String.valueOf(radUDPRequest.getRequestSentTime()));
					
					List<IRadiusAttribute> proxyStates = (ArrayList<IRadiusAttribute>) receivedProxyStates;
					IRadiusAttribute ownProxyState = proxyStates.get(receivedProxyStates.size() - 1);
					
					if (ownProxyState.equals(primaryESIsProxyState)) {
						radUDPRequest.getRadiusPacket().removeAttribute(ownProxyState);
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE,"Removed own proxy state attribute with value: "+ ownProxyState.getStringValue() + " from request for ID: "+ radUDPRequest.getRadiusPacket().getIdentifier());
						}
					} else {
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE,"Own proxy state attribute with value: "+ ownProxyState.getStringValue() +" does not match with proxy state attribute which is added by primary communicator");
						}
					}
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE,"Proxy state attribute will not be removed from request as it is not found in dictionary");
					}
				}

			}

		}
		private boolean hasMultipleCommunicatorsConfigured() {
			return getGroupSize() > 1;
		}
		
		@Override
		public void requestDropped(UDPRequest udpRequest,
				UDPCommunicator udpCommunicator) {
			RadUDPCommunicator secondaryCommunicator = (RadUDPCommunicator) getSecondaryCommunicator(udpCommunicator);
			if (secondaryCommunicator == null) {
				
				if (hasMultipleCommunicatorsConfigured()) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "No other target system found in group to resend request "
								+ "for request id: " + udpRequest.getIdentifier());
					}
				}
				
				responseListner.requestDropped((RadUDPRequest)udpRequest);
				return;
			}
			RadUDPRequest radUDPRequest = (RadUDPRequest) udpRequest;
			RadUDPRequest udpRequestForSecondarySever = new RadUDPRequestImpl(udpRequest.getBytes(), radUDPRequest.getSharedSecret(), new SecondaryResponseListnerImpl(responseListner));
			secondaryCommunicator.handleRadiusRequest(udpRequestForSecondarySever);
		}
	}
	
	private class SecondaryResponseListnerImpl extends UDPResponseListnerImpl{
		public SecondaryResponseListnerImpl(RadResponseListener responseListner) {
			super(responseListner);
		}

		@Override
		public void requestTimeout(UDPRequest udpRequest,
				UDPCommunicator udpCommunicator) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Timeout response received from secondary server: " + udpCommunicator.getName() 
				+ " [" + udpCommunicator.getCommunicatorContext().getIPAddress() + ":" + udpCommunicator.getCommunicatorContext().getPort() + "] "
				+ "for request id: " + udpRequest.getIdentifier());
			}
			
			getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.RADIUS_REQUEST_TIMEOUT, 
					MODULE, "Timeout response received from secondary server: " 
							+ udpCommunicator.getCommunicatorContext().getIPAddress() + ":" 
							+ udpCommunicator.getCommunicatorContext().getPort(),0,
							udpCommunicator.getCommunicatorContext().getIPAddress() + ":" 
									+ udpCommunicator.getCommunicatorContext().getPort());
			
			responseListner.requestTimeout((RadUDPRequest)udpRequest);
		}
		
		@Override
		public void requestDropped(UDPRequest udpRequest,
				UDPCommunicator udpCommunicator) {
			responseListner.requestDropped((RadUDPRequest)udpRequest);
		}
	}

	private ServerContext getServerContext() {
		return this.serverContext;
	}
}
