/**
 * 
 */
package com.elitecore.aaa.rm.conf;

import java.net.InetAddress;
import java.util.List;

import com.elitecore.aaa.radius.conf.BaseRadiusServiceConfiguration;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientDataImpl;

/**
 * @author dhaval.jobanputra
 *
 */
public interface GTPPrimeConfiguration extends BaseRadiusServiceConfiguration {
	public String redirectionIP();
	public long getIdleCommunicationTimeInterval();
	
	public List<GTPPrimeClientDataImpl> getClientList();
	public GTPPrimeClientDataImpl getClient (InetAddress addr);
}
