package com.elitecore.aaa.rm.conf;

import java.net.InetAddress;
import java.util.List;

import com.elitecore.aaa.rm.data.RDRClientData;



public interface RDRClientConfiguration {
	
	public List<RDRClientData> getClientList();
	public RDRClientData getClient (InetAddress addr);
	public void readClientConfiguration();
}
