package com.elitecore.diameterapi.core.common.transport.tcp;

import java.util.concurrent.ScheduledFuture;

import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.core.serverx.internal.tasks.CallableSingleExecutionAsyncTask;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.common.transport.tcp.config.PeerConnectionData;

public interface ConnectorContext {
	
	public int getSocketReceiveBufferSize();
	public int getSocketSendBufferSize();
	
	/**
	 * Handles the incoming packet in the context of caller thread. This method returns when stack
	 * has handled the incoming packet.
	 * 
	 * @param packet the incoming packet to handle
	 * @param handler the connection handler
	 */
	public void executeInSync(Packet packet, NetworkConnectionHandler handler);
	
	/**
	 * Handles the incoming packet outside the context of caller thread. This is a non-blocking method,
	 * returns immediately after submitting the packet to stack for handling. It is not guaranteed that 
	 * the incoming packet is handled when this method returns. 
	 * 
	 * <p>
	 * If sync processing is required then use {@link #executeInSync(Packet, NetworkConnectionHandler)}. 
	 *  
	 * @param packet the incoming packet to handle
	 * @param handler the connection handler
	 */
	public void executeInAsync(Packet packet, NetworkConnectionHandler handler);
	
	public boolean removeConnectionHandler(NetworkConnectionHandler handler);
	public String getNetworkAddress();	
	public int getNetworkPort();
	public PeerConnectionData getPeerConnectionData(String ipAddress);
	public <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(CallableSingleExecutionAsyncTask<T> task);
	public EliteSSLContextExt createEliteSSLContext() throws Exception ;
	public SecurityStandard getDefaultSecurityStandard();

}
