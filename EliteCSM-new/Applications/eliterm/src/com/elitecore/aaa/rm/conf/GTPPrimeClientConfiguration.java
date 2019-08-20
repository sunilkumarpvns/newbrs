/**
 * 
 */
package com.elitecore.aaa.rm.conf;

import java.net.InetAddress;
import java.util.List;

import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientData;




/**
 * @author dhaval.jobanputra
 *
 */
public interface GTPPrimeClientConfiguration {
	public void readClientConfiguration(String systemRedirectionIP);
	public List<GTPPrimeClientData> getClientList();
	public GTPPrimeClientData getClient (InetAddress addr);

}
