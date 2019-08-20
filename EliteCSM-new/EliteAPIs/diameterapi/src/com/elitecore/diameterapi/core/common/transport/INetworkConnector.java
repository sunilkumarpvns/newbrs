/**
 * 
 */
package com.elitecore.diameterapi.core.common.transport;

import com.elitecore.core.commons.tls.EliteSSLParameter;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.diameterapi.core.common.peer.IPeerListener;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.common.transport.tcp.connection.ConnectionFactory;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;

/**
 * @author pulin
 *
 */
public interface INetworkConnector {

	public boolean start(ConnectionFactory connectionFactory);
	
	public void openConnection(IPeerListener peerListener);
	
	public boolean stop();
	
	public String getNetworkAddress();
	
	public int getNetworkPort();
	
	public SocketDetail getBondSocketDetail();

	public ServiceRemarks getRemarks();
	
	public EliteSSLParameter getDefalutSSLParameter();
	
	public SecurityStandard geSecurityStandard();
	
	TransportProtocols getTransportProtocol();
}
