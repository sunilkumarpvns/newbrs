package com.elitecore.aaa.radius.service.handlers;

import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * This class is responsible for handling the request based on the service policy selected.
 * This class is not meant to be instantiated from outside. It is provided by the service policy
 * selected, it acts as the factory for executor.
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request
 * @param <V> type of response
 */
public interface RequestExecutor<T, V> {
	public void startRequestExecution(ISession session);
	public void resumeRequestExecution(ISession session);
}
