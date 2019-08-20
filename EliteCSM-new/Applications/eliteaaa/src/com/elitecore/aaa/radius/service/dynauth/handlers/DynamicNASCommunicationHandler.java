package com.elitecore.aaa.radius.service.dynauth.handlers;

import java.net.UnknownHostException;
import java.util.List;

import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthRequest;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthResponse;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthServiceContext;
import com.elitecore.aaa.radius.systemx.esix.udp.DynamicNasExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommGroupImpl;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommunicatorManagerImpl;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.util.url.URLData;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * <p>
 * Responsible for handling dynamic NAS creation and sending the request to that created NAS</p>
 * 
 * <p>This class will come into picture when COA request or DM request received contains attribute either 
 * NAS_IP_ADDRESS(0:4) or NAS_IPV6_ADDRESS(0:95). It will use this received address and tries to fetch
 * client data. Client data would be available if the received IP address is configured in clients configuration.
 * If client data is not available then the request will be dropped as Unknown Client. Otherwise,
 * use this client data and generate new external system that will be used to create UDP Communicator
 * through {@link RadUDPCommunicatorManagerImpl}</p>
 * 
 * <p>This created UDP Communicator is used to send the request to the newly created external system.
 * Handling of the sending request and receiving the response is done with the help of {@link RadUDPCommGroupImpl}</p>
 * 
 * <pre>
 * 
 *     DYNAMIC NAS COMMUNICATOR
 *     +-----------------------+
 *     |                       |
 *     |      +---------+      |
 * REQ |      |         |      |  DYNAMIC NAS
 * --->|      | HANDLER |      |--------------> DROP 
 *     |      |         |      |   NOT FOUND
 *     |      +---------+      |
 *     |        YES |          |
 *     +------------|----------+
 *                  |
 *                  V
 *           POST PROCESSING                    
 *  
 * </pre>
 * 
 * @author kuldeep panchal
 *
 */
public class DynamicNASCommunicationHandler implements RadDynAuthServiceHandler {
	
	private static final String MODULE = "DYN-NAS-COMM-HANDL";

	private final RadDynAuthServiceContext serviceContext;
	
	public DynamicNASCommunicationHandler(RadDynAuthServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	@Override
	public void init() throws InitializationFailedException {
	
	}
	
	@Override
	public void handleRequest(RadDynAuthRequest request, RadDynAuthResponse response, ISession session) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Communicating via dynamic NAS");
		}
		
		String receivedIPAddress = fetchNasIpAddress(request);
		
		if (Strings.isNullOrEmpty(receivedIPAddress)) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Skipping Dynamic NAS Selection process, " +
						"Reason: Nas-IP-Address (0:4) or Nas-IPv6-Address (0:95) attribute not found in request");
			}
			return;
		}
		
		UDPCommunicator udpCommunicator = createUdpCommunicatorFor(receivedIPAddress);

		if (udpCommunicator == null) {
			return;
		}
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Dynamic NAS Communicator: " + udpCommunicator.getName() + 
												" is selected with IP Address: " + receivedIPAddress);
		}			
		
		handleRequest(request, response, udpCommunicator, session);
	}

	private void handleRequest(RadDynAuthRequest request,
								RadDynAuthResponse response, 
								UDPCommunicator udpCommunicator, ISession session) {
		handlePreRequest(request);
		
		RadUDPCommGroupImpl udpCommGroup = new RadUDPCommGroupImpl(getServiceContext().getServerContext());
		udpCommGroup.addCommunicator(udpCommunicator);
		
		RadiusProcessHelper.onExternalCommunication(request, response);
		
		udpCommGroup.handleRequest(request.getRequestBytes(true),
				response.getClientData().getSharedSecret(request.getPacketType()), 
				new RadResponseListnerImpl(request, response), session);
	}

	/*
	 * Try to fetch IP Address from NAS_IP_ADDRESS or NAS_IPV6_ADDRESS attribute received in the request.
	 */
	private String fetchNasIpAddress(RadDynAuthRequest request) {
		IRadiusAttribute nasIpAddress = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS, true);
		if (nasIpAddress == null) {
			nasIpAddress = request.getRadiusAttribute(RadiusAttributeConstants.NAS_IPV6_ADDRESS, true);
		}
		
		if (nasIpAddress == null) {
			return null;
		}
		
		return nasIpAddress.getStringValue();
	}
	
	private UDPCommunicator createUdpCommunicatorFor(String receivedIPAddress) {

		UDPCommunicator communicator = null;
		
		RadClientData clientData = getServiceContext().getServerContext()
									.getServerConfiguration().getRadClientConfiguration()
									.getClientData(receivedIPAddress);
		
		if (clientData == null) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Dynamic NAS communication failed, " +
						"Reason: Unknown client with IP Address: " + receivedIPAddress);
			}
			return communicator;
		}
		
		if (clientData.getDynauthPort().isPresent() == false) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Dynamic NAS Communication failed, "
						+ "Reason: No Dynamic Authorization port configured for client IP: "
						+ receivedIPAddress + " in client profile: " 
						+ clientData.getProfileName());
			}
			return communicator;
		}


		URLData url = new URLData();
		url.setHost(receivedIPAddress);
		url.setPort(clientData.getDynauthPort().get());

		DynamicNasExternalSystemData radiusExternalSystemData = null;
		
		try {
			radiusExternalSystemData =  DynamicNasExternalSystemData.
					create(url, clientData.getTimeout(), clientData.getSharedSecret(RadiusConstants.COA_REQUEST_MESSAGE),  
							clientData.getSupportedAttributesStrCOA(), clientData.getUnsupportedAttributesStrCOA(), 
							clientData.getSupportedAttributesStrDM(), clientData.getUnsupportedAttributesStrDM());


			communicator = getServiceContext().getServerContext().getRadUDPCommunicatorManager()
					.findCommunicatorByURLOrCreate(url, 
							getServiceContext().getServerContext(), 
							radiusExternalSystemData);
			
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to create Dynamic NAS: " + radiusExternalSystemData.getName() + 
					" , Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);

		} catch (UnknownHostException e) {
			// will not occur. We will always have an IP address
		}		
		return communicator;
	}

	private void handlePreRequest(RadDynAuthRequest request) {
		request.setParameter(RadiusConstants.NAS_COMMUNICATOR_SELECTED, true);
	}

	private RadDynAuthServiceContext getServiceContext() {
		return serviceContext;
	}
	
	@Override
	public boolean isEligible(RadDynAuthRequest request, RadDynAuthResponse response) {
		return true;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
	
	class RadResponseListnerImpl implements RadResponseListener {
		final RadDynAuthRequest request;
		final RadDynAuthResponse response;
		
		public RadResponseListnerImpl(RadDynAuthRequest request,RadDynAuthResponse response) {
			this.request = request;
			this.response = response;
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Unable to forward request to target system");
			}
			((RadDynAuthServiceContext)getServiceContext()).submitAsyncRequest(request, response, 
					new AsyncRequestExecutorImpl(radUDPRequest, null));
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Response received from Proxy Server: " + radUDPResponse);
			}
			((RadDynAuthServiceContext)getServiceContext()).submitAsyncRequest(request, response, 
					new AsyncRequestExecutorImpl(radUDPRequest,radUDPResponse));
		}
		
		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Request Timeout Response Received");
			}
			((RadDynAuthServiceContext)getServiceContext()).submitAsyncRequest(request, response, 
					new AsyncRequestExecutorImpl(radUDPRequest, null));
		}
	}
	
	private class AsyncRequestExecutorImpl implements AsyncRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> {
		final RadUDPRequest udpRequest;
		final RadUDPResponse udpResponse;
		
		public AsyncRequestExecutorImpl(RadUDPRequest udpRequest,RadUDPResponse udpResponse) {
			this.udpRequest = udpRequest;
			this.udpResponse = udpResponse;
		}

		@Override
		public void handleServiceRequest(RadDynAuthRequest radDynAuthRequest,
				RadDynAuthResponse radDynAuthResponse) {
			if (udpResponse == null) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "No Response Received from Target System, Dropping request");
				}
				RadiusProcessHelper.dropResponse(radDynAuthResponse);
				return;
			}
			
			IRadiusPacket targetServerResponse = udpResponse.getRadiusPacket();

			//	re-encrypting value of any encryptable attribute
			((RadiusPacket)targetServerResponse).reencryptAttributes(udpRequest.getRadiusPacket().getAuthenticator(),
					udpRequest.getSharedSecret(), radDynAuthRequest.getAuthenticator(), 
					radDynAuthResponse.getClientData().getSharedSecret(radDynAuthRequest.getPacketType()));
			
			targetServerResponse.refreshPacketHeader();

			List<IRadiusAttribute> radiusAttributes = (List<IRadiusAttribute>) targetServerResponse.getRadiusAttributes();
			for (IRadiusAttribute radAttribute : radiusAttributes) {
				radDynAuthResponse.addAttribute(radAttribute);
			}
			radDynAuthResponse.setPacketType(targetServerResponse.getPacketType());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
}
