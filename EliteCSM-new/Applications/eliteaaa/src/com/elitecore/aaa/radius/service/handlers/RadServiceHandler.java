package com.elitecore.aaa.radius.service.handlers;

import com.elitecore.aaa.core.services.handler.ServiceHandler;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.policy.handler.AsyncRadServiceHandler;

/**
 * 
 * This interface is meant to encapsulate some processing that takes place on 
 * the request and response. All the implementer classes do processing in a 
 * synchronous manner. For a handler that needs to support asynchronous processing
 * refer {@link AsyncRadServiceHandler}.   
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request packet
 * @param <V> type of response packet
 */
public interface RadServiceHandler<T extends RadServiceRequest, V extends RadServiceResponse> extends ServiceHandler<T, V> {
	
}
