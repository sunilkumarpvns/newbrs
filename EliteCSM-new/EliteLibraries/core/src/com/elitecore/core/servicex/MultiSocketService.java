package com.elitecore.core.servicex;

import java.net.InetAddress;
import java.util.List;

import com.elitecore.core.util.url.SocketDetail;


/**
 * Every network service that can run on multiple sockets should implement this interface.
 * 
 * <p>
 * If a service is running on multiple sockets then at time of responding, it is required for service to know on which socket 
 * request was received. So it is necessary to preserve IP:Port as a {@link SocketDetail} while generating Request 
 * <a href="https://jira.eliteaaa.com:8443/browse/ELITEAAA-2469">(JIRA-2469)</a>.
 * </p>
 * 
 * 
 * @author malav.desai
 *
 * @param <T> type of <b>{@link ServiceRequest}</b>
 * @param <V> type of <b>{@link ServiceResponse}</b>
 */

public interface MultiSocketService<T extends ServiceRequest, V extends ServiceResponse> extends EliteService {
	
	/**
	 * 
	 * @return list of socket detail configured by user for service to start.
	 */
	List<SocketDetail> getSocketDetails();
	
	/**
	 * 
	 * @return list of socket detail on which service is running.
	 */
	List<SocketDetail> getListeningSocketDetails();
	
	/**
	 * Should return a new concrete implementation of {@link ServiceRequest} applicable to the service   
	 * 
	 * @param sourceAddress InetAddress from which the request was received
	 * @param sourcePort port from which the request was received 
	 * @param requestBytes request bytes received on socket {@code serverSocketDetail}
	 * @param serverSocketDetail SocketDetail (IPAddress:Port) of source  
	 * @return type of <b>{@link ServiceRequest}</b>
	 */
	T formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, 
			byte[] requestBytes, SocketDetail serverSocketDetail);
	
	/**
	 * Should return a new concrete implementation of {@link ServiceResponse} applicable to the service
	 * 
	 * @param serviceRequest type of <b>{@link ServiceRequest}</b> 
	 * @return type of <b>{@link ServiceResponse}</b> 
	 */
	V formServiceSpecificResposne(T serviceRequest);
}
