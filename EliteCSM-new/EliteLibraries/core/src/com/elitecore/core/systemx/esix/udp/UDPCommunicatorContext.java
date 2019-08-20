package com.elitecore.core.systemx.esix.udp;

import com.elitecore.core.systemx.esix.ESCommunicatorContext;

public interface UDPCommunicatorContext extends ESCommunicatorContext {
	public String getIPAddress();
	public int getPort();
	public UDPExternalSystemData getExternalSystem();
	public String getName();
	public void responseReceived(UDPRequest udpRequest,UDPResponse udpResponse);
	public void highResponseTimeReceived(UDPRequest udpRequest, UDPResponse udpResponse, int endToEndResponseTime);
	public void removeAttributes(UDPRequest udpRequest);
	public void incrementTotalRequests();
	public void incrementSuccessResponseCount();
	public void incrementErrorResponseCount();
	public void updateAverageResponseTime(long responseTime);
}
