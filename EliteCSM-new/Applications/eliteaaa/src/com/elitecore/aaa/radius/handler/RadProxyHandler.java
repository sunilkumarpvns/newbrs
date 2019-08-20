package com.elitecore.aaa.radius.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommGroup;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommGroupImpl;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadProxyHandler<T extends RadServiceRequest, V extends RadServiceResponse> implements ReInitializable{

	private static final String MODULE = "RAD-PROXY-HANDLER";

	private RadServiceContext<T, V> serviceContext;
	private Map<String,RadUDPCommGroup> acctCommunicatorGrpMap;
	private Map<String,RadUDPCommGroup> authCommunicatorGrpMap;
	private int ESIType;

	public RadProxyHandler(RadServiceContext<T, V> serviceContext, int ESIType){
		acctCommunicatorGrpMap = new HashMap<String, RadUDPCommGroup>();
		authCommunicatorGrpMap = new HashMap<String, RadUDPCommGroup>();
		this.serviceContext = serviceContext;
		this.ESIType = ESIType;
	}

	public void init(){
		List<DefaultExternalSystemData> ESList = getServiceContext().getServerContext().getServerConfiguration().getRadESConfiguration().getESListByType(ESIType);
		if(ESList != null){
			if(ESIType == RadESTypeConstants.RAD_AUTH_PROXY.type){
				for (DefaultExternalSystemData es : ESList) {
					List<String> realmNames = es.getRealmNames();
					for(int i=0;i<realmNames.size();i++){
						if(authCommunicatorGrpMap.get(realmNames.get(i)) == null){
							RadUDPCommGroup udpCommunicatorGrp = new RadUDPCommGroupImpl(serviceContext,null);
							try{
								UDPCommunicator esCommunicator = getServiceContext().getServerContext().getRadUDPCommunicatorManager().findCommunicatorByIDOrCreate(es.getUUID(), serviceContext.getServerContext(), es);
								udpCommunicatorGrp.addCommunicator(esCommunicator,1);
								authCommunicatorGrpMap.put(realmNames.get(i), udpCommunicatorGrp);
							}catch(InitializationFailedException ex){
								if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
									LogManager.getLogger().debug(MODULE, "Failed to initilize UDP communicator :"+es.getName()+" Reason :"+ex.getMessage());
							}
						}else{
							RadUDPCommGroup udpCommunicatorGrp = authCommunicatorGrpMap.get(realmNames.get(i));
							try{
								UDPCommunicator esCommunicator = getServiceContext().getServerContext().getRadUDPCommunicatorManager().findCommunicatorByIDOrCreate(es.getUUID(), serviceContext.getServerContext(), es);
								udpCommunicatorGrp.addCommunicator(esCommunicator,1);
							}catch(InitializationFailedException ex){
								if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
									LogManager.getLogger().debug(MODULE, "Failed to initilize UDP communicator :"+es.getName()+" Reason :"+ex.getMessage());
							}
						}
					}
				}
			}else if(ESIType == RadESTypeConstants.RAD_ACCT_PROXY.type){
							for (DefaultExternalSystemData es : ESList) {
								List<String> realmNames = es.getRealmNames();
								for(int i=0;i<realmNames.size();i++){
									if(acctCommunicatorGrpMap.get(realmNames.get(i)) == null){
										RadUDPCommGroup udpCommunicatorGrp = new RadUDPCommGroupImpl(serviceContext,null);
										try{
											UDPCommunicator esCommunicator = getServiceContext().getServerContext().getRadUDPCommunicatorManager().findCommunicatorByIDOrCreate(es.getUUID(), serviceContext.getServerContext(), es);
											udpCommunicatorGrp.addCommunicator(esCommunicator,1);
											acctCommunicatorGrpMap.put(realmNames.get(i), udpCommunicatorGrp);
										}catch(InitializationFailedException ex){
											if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
												LogManager.getLogger().debug(MODULE, "Failed to initilize UDP communicator :"+es.getName()+" Reason :"+ex.getMessage());
										}
									}else{
										RadUDPCommGroup udpCommunicatorGrp = acctCommunicatorGrpMap.get(realmNames.get(i));
										try{
											UDPCommunicator esCommunicator = getServiceContext().getServerContext().getRadUDPCommunicatorManager().findCommunicatorByIDOrCreate(es.getUUID(), serviceContext.getServerContext(), es);
											udpCommunicatorGrp.addCommunicator(esCommunicator,1);
										}catch(InitializationFailedException ex){
											if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
												LogManager.getLogger().debug(MODULE, "Failed to initilize UDP communicator :"+es.getName()+" Reason :"+ex.getMessage());
										}
									}
								}
							}
			}
		}else{
			LogManager.getLogger().debug(MODULE, "The Proxy ESI List is empty for ESI type:" + RadESTypeConstants.getName(ESIType));
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
	
	}
	
	public void handleRequest(T serviceRequest, V serviceResponse){
		LogManager.getLogger().info(MODULE, "Handling proxy request");
		serviceResponse.setFurtherProcessingRequired(false);
		serviceResponse.setProcessingCompleted(false);	
		String userName = serviceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME).getStringValue();
		String realmName = userName.substring(userName.lastIndexOf('@')+1, userName.length());
		if(serviceRequest.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
			if(authCommunicatorGrpMap.get(realmName) != null){
				serviceRequest.stopFurtherExecution();
				serviceResponse.setFurtherProcessingRequired(false);
				serviceResponse.setProcessingCompleted(false);
				authCommunicatorGrpMap.get(realmName).handleRequest(serviceRequest.getRequestBytes(true),
						serviceResponse.getClientData().getSharedSecret(serviceRequest.getPacketType()),
						new RadResponseListnerImpl(serviceRequest, serviceResponse), HazelcastRadiusSession.RAD_NO_SESSION);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "No Auth communicator with realm name: " + realmName + " found.");
				}
				serviceResponse.markForDropRequest();
			}
		}else if(serviceRequest.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
			if(acctCommunicatorGrpMap.get(realmName) != null){
				serviceRequest.stopFurtherExecution();
				serviceResponse.setFurtherProcessingRequired(false);
				serviceResponse.setProcessingCompleted(false);
				acctCommunicatorGrpMap.get(realmName).handleRequest(serviceRequest.getRequestBytes(true),
						serviceResponse.getClientData().getSharedSecret(serviceRequest.getPacketType()),
						new RadResponseListnerImpl(serviceRequest, serviceResponse), HazelcastRadiusSession.RAD_NO_SESSION);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "No Acct communicator with realm name: " + realmName + " found.");
				}
				serviceResponse.markForDropRequest();
			}
		}
	}

	class RadResponseListnerImpl implements RadResponseListener{

		T serviceRequest;
		V serviceResponse;

		public RadResponseListnerImpl(T serviceRequest, V serviceResponse){
			this.serviceRequest = serviceRequest;
			this.serviceResponse = serviceResponse;
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Response received from Proxy Server: " + radUDPResponse);
			getServiceContext().submitAsyncRequest(serviceRequest, serviceResponse, new AsyncRequestExecutorImpl(radUDPRequest,radUDPResponse));
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Unable to forward request to target system");
			getServiceContext().submitAsyncRequest(serviceRequest, serviceResponse, new AsyncRequestExecutorImpl(radUDPRequest,null));
		}

		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Request Timeout Response Received");
			getServiceContext().submitAsyncRequest(serviceRequest, serviceResponse, new AsyncRequestExecutorImpl(radUDPRequest,null));

		}
	}

	private class AsyncRequestExecutorImpl implements AsyncRequestExecutor<T, V> {
		private RadUDPRequest udpRequest;
		private RadUDPResponse udpResponse;
		public AsyncRequestExecutorImpl(RadUDPRequest udpRequest, RadUDPResponse udpResponse){
			this.udpRequest = udpRequest; 
			this.udpResponse = udpResponse;
		}

		@Override
		public void handleServiceRequest(T serviceRequest,
				V serviceResponse) {
			RadServiceResponse radServiceResponse = (RadServiceResponse)serviceResponse;
			RadServiceRequest radServiceRequest = (RadServiceRequest)serviceRequest;
			if(udpResponse == null){
				LogManager.getLogger().info(MODULE, "No Response Received from Target System, Dropiing request");
				radServiceResponse.markForDropRequest();
				radServiceResponse.setFurtherProcessingRequired(false);
				radServiceResponse.setProcessingCompleted(true);
				return;
			}

			
			IRadiusPacket proxyResponse = udpResponse.getRadiusPacket();
            //	re-encrypting value of any encryptable attribute
			((RadiusPacket)proxyResponse).reencryptAttributes(udpRequest.getRadiusPacket().getAuthenticator(), udpRequest.getSharedSecret(), ((RadServiceRequest)serviceRequest).getAuthenticator(), radServiceResponse.getClientData().getSharedSecret(radServiceRequest.getPacketType()));
			proxyResponse.refreshPacketHeader();

						
			List<IRadiusAttribute> radiusAttributes = (List<IRadiusAttribute>) proxyResponse.getRadiusAttributes();
			for(IRadiusAttribute radAttribute:radiusAttributes){
				if(radAttribute.getVendorID() == 0 && radAttribute.getID() == RadiusAttributeConstants.REPLY_MESSAGE){
					radServiceResponse.setResponseMessage(radAttribute.getStringValue());
				}else{
					radServiceResponse.addAttribute(radAttribute);
				}
			}
			radServiceResponse.setPacketType(proxyResponse.getPacketType());
			radServiceResponse.setProcessingCompleted(true);
			radServiceResponse.setFurtherProcessingRequired(false);
			
		}

	}

	private RadServiceContext<T, V> getServiceContext(){
		return serviceContext;
	}
}
