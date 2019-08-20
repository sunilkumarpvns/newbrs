/**
 * 
 */
package com.elitecore.diameterapi.core.common.transport;

import java.util.Map;

import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;

/**
 * @author pulin
 *
 */
public interface NetworkConnectionEventListener {

	public void connectionEstablished();
	
	public void connectionBreak(NetworkConnectionHandler connectionHandler, ConnectionEvents event);
	public void connectionFailure(NetworkConnectionHandler connectionHandler);

	void connectionDPR(Map<PeerDataCode, String> eventParam,
			ConnectionEvents event);

	void connectionCreated(NetworkConnectionHandler connectionHandler);
	
}
