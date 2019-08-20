package com.elitecore.aaa.core.services.handler;

import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

/**
 * The root interface in the handler hierarchy. It represents a sub-process
 * in the flow of request handling and forming a response to be sent. On reception,
 * the request can flow through series of handlers to form a valid response. 
 * <p>
 * Typically when a request is received, it flows through many composed set of handlers,
 * such as a chain of handlers, to form a response.
 * 
 * @param <T> type of request packet
 * @param <V> type of response packet
 * 
 * @author narendra.pathai
 */
public interface ServiceHandler<T extends ServiceRequest, V extends ServiceResponse> extends ReInitializable {
	/**
	 * This method is called once when the handler is created for allowing it to create
	 * all the dependent components. The implementation can throw {@link InitializationFailedException}
	 * when there is any problem in creating its components and it is not in state of 
	 * handling the request.
	 * 
	 * @throws InitializationFailedException when there is any problem in initializing 
	 * the handler
	 */
	public void init() throws InitializationFailedException;
	
	/**
	 * Allows to intercept the incoming request and apply some business logic to 
	 * decide whether the {@link #handleRequest(T, V, ISession)}
	 * method should be called on this handler or not . 
	 * 
	 * @param request the request packet
	 * @param response the response packet
	 * @return true if this handler should be applied on incoming request
	 */
	public boolean isEligible(T request, V response);
	
	/**
	 * This is where the processing for the handler should take place, called only if 
	 * {@link #isEligible(T, V)}
	 * returns true. It is the responsibility of the implementation to halt further
	 * execution of the request in case of any external communication using 
	 * {@link RadiusProcessHelper#onExternalCommunication(T, V)}
	 *  
	 * @param request the request packet
	 * @param response the response packet
	 * @param session session details
	 */
	public void handleRequest(T request, V response, ISession session);
	
	/**
	 * This method allows the handler to check the health and return the result 
	 * whether it is in a healthy condition or not. A typical use case is when
	 * some transient components, such as drivers or communicators, are used in the 
	 * handler then they can be alive or dead at the time of request arrival. In that 
	 * case this method should return {@code true}, to indicate that it is unhealthy.
	 * 
	 * @return true if some transient components are dead, false otherwise
	 */
	public boolean isResponseBehaviorApplicable();
}
