package com.elitecore.core.servicex;

import java.net.InetAddress;

import com.elitecore.core.util.url.SocketDetail;

/**
 * Every network service that can run on single socket should implement this interface.
 * <a href="https://jira.eliteaaa.com:8443/browse/ELITEAAA-2469">(JIRA-2469)</a>.
 * 
 * @author malav.desai
 *
 * @param <T> type of <b>{@link ServiceRequest}</b>
 * @param <V> type of <b>{@link ServiceResponse}</b>
 * @version 6.6
 */
public interface UniSocketService<T extends ServiceRequest, V extends ServiceResponse> extends EliteService {

	/**
	 * 
	 * @return SocketDetail list of socket detail on which service is running.
	 */
	public SocketDetail getSocketDetail();
	
	/**
	 * Should return a new concrete implementation of {@link ServiceRequest} applicable to the service   
	 * 
	 * @param sourceAddress InetAddress from which the request was received
	 * @param sourcePort port from which the request was received 
	 * @param requestBytes request bytes received on socket {@code serverSocketDetail}
	 * @return type of <b>{@link ServiceRequest}</b>
	 */
	public T formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, byte[] requestBytes);
	
	/**
	 * Should return a new concrete implementation of {@link ServiceResponse} applicable to the service
	 * 
	 * @param serviceRequest type of <b>{@link ServiceRequest}</b>
	 * @return type of <b>{@link ServiceResponse}</b>
	 */
	public V formServiceSpecificResposne(T serviceRequest);
}