package com.elitecore.core.systemx.esix.udp.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.udp.CommunicationHandler;
import com.elitecore.core.systemx.esix.udp.SessionLimitReachedException;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorContext;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;

public abstract class CommunicationHandlerImpl implements CommunicationHandler {
	
	private static final String MODULE = "COMM-HDLR";
	private static final int PACKET_CAPACITY = 4096;
	private DatagramChannel datagramChannel;
	private int portNumber;
	private Queue<Integer> identifierQueue;
	private UDPRequest[] sentItems;
	private UDPCommunicatorContext communicatorContext;
	private long lastRequestResponseTimeDiff; 
	
	public CommunicationHandlerImpl(UDPCommunicatorContext communicatorContext) {
		this.communicatorContext = communicatorContext;
		this.identifierQueue = new ConcurrentLinkedQueue<Integer>();
		sentItems = new UDPRequest[getMaxIdentifier() + 1];
	}
	
	@Override
	public int getPortNumber() {
		return portNumber;
	}

	@Override
	public void init(DatagramChannel datagramChannel) throws InitializationFailedException {
		this.datagramChannel = datagramChannel;
		this.portNumber = datagramChannel.socket().getLocalPort();
		for(int cntr = 1;cntr <= getMaxIdentifier();cntr++){
			identifierQueue.offer(cntr);
		}
	}
	
	@Override
	public void handleRequest(UDPRequest request)
			throws SessionLimitReachedException {
		
		Integer identifier = getNextIdentifier(request);
		
		if(identifier == null)
			throw new SessionLimitReachedException("No Identifier Left to send request");

		ByteBuffer buffer = ByteBuffer.allocate(PACKET_CAPACITY);
		
		request.setRequestSentTime(System.currentTimeMillis());
		preHandleRequest(request, identifier);
		
		//Send Request to Target System
		sentItems[identifier] = request;
		request.resetRetryCount();
		try {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Sending request with ID: " + identifier+" to External system "+communicatorContext.getExternalSystem().getName()+"("+communicatorContext.getIPAddress()+":"+communicatorContext.getPort()+") "+request);
			
			DatagramPacket datagramPacket = createDatagramPacket(request.getBytes());
			
			buffer.put(datagramPacket.getData());
			buffer.flip();
			
			datagramChannel.send(buffer, datagramPacket.getSocketAddress());
			
		} catch (IOException e) {
			LogManager.getLogger().trace(MODULE, "UDP Socket Status: ( Closed = " + datagramChannel.socket().isClosed() + " )");
			if(datagramChannel.socket().isClosed()) {
				LogManager.getLogger().warn(MODULE, "Removing this socket from binded port list. There may be more port available.");
				//TODO Take proper Action
			}
			LogManager.getLogger().warn(MODULE, "Problem sending request to target system. Reason: " + e.getMessage());
		}
		postHandleRequest(request);
	
		
	}
	private DatagramPacket createDatagramPacket(byte[] buffer) {
		return new DatagramPacket(buffer, buffer.length, communicatorContext.getExternalSystem().getIPAddress(), communicatorContext.getExternalSystem().getPort());
	}
	protected abstract UDPResponse createUDPResponsePacket(byte[] responseBytes,String esiName);
	
	public abstract int getMaxIdentifier();
	
	//this method will return the present free identifier and will throw SessionLimitReachedException if no identifier is free
	public Integer getNextIdentifier(UDPRequest request){
		return identifierQueue.poll();
	}
	
	//this method will allow the implementers to free the identifiers whose response have been received
	public void releaseIdentifier(int identifier){
		identifierQueue.offer(identifier);
	}
	
	
	protected UDPCommunicatorContext getCommunicatorContext() {
		return communicatorContext;
	}
	
	protected void preHandleRequest(UDPRequest udpRequest,int identifier){
		
	}
	
	protected void postHandleRequest(UDPRequest udpRequest){
		
	}
	
	protected void postHandleResponse(UDPResponse udpResponse){
		
	}

	public void handleResponse(UDPResponse udpResponse) throws Exception {
		UDPRequest udpRequest = sentItems[udpResponse.getIdentifier()];
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Response received for ID: " + udpResponse.getIdentifier() + " Response: " + udpResponse);

		if(udpRequest != null){	
			CommunicationHandlerImpl.this.lastRequestResponseTimeDiff = System.currentTimeMillis()- udpRequest.getRequestSentTime();
			postHandleResponse(udpResponse);
			if(udpRequest.validate(udpResponse)){

				int endToEndResponseTime =  (int) (System.currentTimeMillis()- udpRequest.getRequestSentTime());

				//updating the statistics for average response time
				communicatorContext.updateAverageResponseTime(endToEndResponseTime);

				if(endToEndResponseTime > (communicatorContext.getExternalSystem().getCommunicationTimeout()/2)) {
					getCommunicatorContext().highResponseTimeReceived(udpRequest, udpResponse, endToEndResponseTime);
				}

				sentItems[udpResponse.getIdentifier()] = null;
				releaseIdentifier(udpResponse.getIdentifier());

				//incrementing the success statistic count
				communicatorContext.incrementSuccessResponseCount();

				communicatorContext.responseReceived(udpRequest, udpResponse);							
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in validating response. Dropping Response");
				actionOnResponseDropped(udpResponse);

				//incrementing the error response count
				communicatorContext.incrementErrorResponseCount();
			}
		} else {
			LogManager.getLogger().error(MODULE, "Unknown response (Probably Timeout) received from " + communicatorContext.getName() + "(" +communicatorContext.getIPAddress() + ":"+ communicatorContext.getPort()+ ")" + ", Id=" + udpResponse.getIdentifier() + ". Discarding response.");
		}
	}
	
	@Override
	public List<UDPRequest> getTimeoutRequests() {
		long lTimeOut = getCommunicatorContext().getExternalSystem().getCommunicationTimeout();
		List<UDPRequest> timeoutRequests = new ArrayList<UDPRequest>();
		long currentTimeInMilli = System.currentTimeMillis();
		for(int i=0;i<getMaxIdentifier();i++){
			UDPRequest udpRequest = sentItems[i];
			if(udpRequest == null){
				continue;
			}
			if((currentTimeInMilli - udpRequest.getLastUpdateTime()) >= lTimeOut){
				if(udpRequest.getRetryCount() < getCommunicatorContext().getExternalSystem().getRetryLimit() && udpRequest.getResponseListener() != null){
					//Resend request
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No response from server " + getCommunicatorContext().getIPAddress() + ":" + 
								getCommunicatorContext().getPort() + " (timed out), new attempt (#" + (udpRequest.getRetryCount() + 1) + ")");
					try {
						udpRequest.incrementRetryCount();
						DatagramPacket datagramPacket = createDatagramPacket(udpRequest.getBytes());
						byte[] bytes = datagramPacket.getData();
						ByteBuffer retryBuffer = ByteBuffer.allocate(PACKET_CAPACITY);
						retryBuffer.put(bytes);
						retryBuffer.flip();
						datagramChannel.send(retryBuffer, datagramPacket.getSocketAddress());
						generateRetryAlert("String No response from server " + getCommunicatorContext().getIPAddress() + ":" + getCommunicatorContext().getPort() + " (timed out), new attempt (#" + (udpRequest.getRetryCount() + 1));
						actionOnRetrasmissionsRequest(udpRequest);
						udpRequest.setLastUpdateTime(System.currentTimeMillis());
						actionOnTimeout(udpRequest);
						continue;
					} catch (IOException e) {
						//If any problem occured during sending request, that request will be added to timeout requests list
					}
				}
				sentItems[i] = null;
				releaseIdentifier(udpRequest.getIdentifier());
				if(udpRequest.getResponseListener() != null) {
					timeoutRequests.add(udpRequest);
					actionOnTimeout(udpRequest);
				}
					
			}
		}
		return timeoutRequests;
	}
	
	protected void generateRetryAlert(String message) {
		
	}
	
	public void shutdown() {
		try {
			datagramChannel.close();
		}catch(Exception e) {
			LogManager.getLogger().warn(MODULE, e.getMessage());
		}
	}
	
	private boolean isSentItemListEmpty() {
		boolean flag = true;
		for(int cnt=0; cnt < sentItems.length; cnt++) {
			if(sentItems[cnt] != null) {
				flag = false;
				break;
			}
		}
		
		return flag;
	}
	
	public void actionOnTimeout(UDPRequest udpRequest) {
		// default implementation
	}
	
	public final void actionOnRetrasmissionsRequest(UDPRequest udpRequest) {
		//incrementing the total requests count
		communicatorContext.incrementTotalRequests();
		
		actionOnRetransmission(udpRequest);
	}
	
	protected void actionOnRetransmission(UDPRequest udpRequest){
		// default implementation
	}
	
	public void actionOnResponseDropped(UDPResponse udpResponse) {
		// default implementation
	}
	public long getLastRequestResponseTimeDiff() {
		return lastRequestResponseTimeDiff;
	}
	
}
