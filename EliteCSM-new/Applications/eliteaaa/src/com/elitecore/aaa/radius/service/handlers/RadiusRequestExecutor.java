package com.elitecore.aaa.radius.service.handlers;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.elitecore.aaa.core.services.handler.ServiceHandler;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

/**
 * 
 * Executes the request handling process and forming response, by passing the request
 * through the set of handlers in the workflow. This class serves as the store of
 * resumable handler sessions.
 * <p>
 * When there is any external communication and further request execution is stalled,
 * then executor stores all the pending resumable handlers and 
 * starts resumption of request in LIFO manner, passing the request to each hanlder in 
 * stack to allow the handler to resume its processing.
 * <p>
 * Take an example of {@link RadiusChainHandler}, which has a chain of handlers that need
 * to execute, if second handler does some external communication then the chain processing
 * is halted at second handler. So the chain handler will be at ToS (Top of Stack) and
 * when resumption occurs the processing will start from ToS, 
 * effectively starting the chain handler processing from third handler.  
 * <p>
 * This class is thread safe.
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request packet
 * @param <V> type of response packet
 * @see ResumableServiceHandler
 */
@ThreadSafe
public class RadiusRequestExecutor<T extends ServiceRequest, V extends ServiceResponse> implements RequestExecutor<T, V> {
	
	private final T request;
	private final V response;
	private final ServiceHandler<T, V> handler;
	
	@GuardedBy("this")
	private ArrayDeque<ResumableServiceHandler<T, V>> resumableHandlers = new ArrayDeque<ResumableServiceHandler<T, V>>(10);
	@GuardedBy("this")
	private ArrayDeque<HandlerSession> handlerSessions = new ArrayDeque<HandlerSession>(10);
	
	/**
	 * Creates a new request executor to handle the request execution, passing
	 * the request to the handler supplied.
	 * 
	 * @param handler a non-null handler from which execution will start
	 * @param request a non-null request packet
	 * @param response a non-null response packet
	 * @throws NullPointerException is any argument is null
	 */
	public RadiusRequestExecutor(@Nonnull ServiceHandler<T, V> handler,
			@Nonnull T request, @Nonnull V response) {
		this.handler = checkNotNull(handler, "handler is null");
		this.request = checkNotNull(request, "request is null");
		this.response = checkNotNull(response, "response is null");
	}

	/**
	 * Starts the request execution passing the request to the handler supplied
	 */
	@Override
	public synchronized void startRequestExecution(ISession session) {
		this.handler.handleRequest(request, response, session);
	}

	/**
	 * Resumes the request execution if any resumable handlers are pending to be
	 * executed due to external communication 
	 */
	@Override
	public synchronized void resumeRequestExecution(ISession session) {
		while (resumableHandlers.isEmpty() == false){
			if (shouldContinueExecution() == false) {
				break;
			}
			resumableHandlers.peek().resumeRequest(request, response, session);
		}
	}
	
	private boolean shouldContinueExecution() {
		return response.isFurtherProcessingRequired() 
		&& !response.isMarkedForDropRequest();
	}

	public synchronized void saveHandlerSession(@Nonnull HandlerSession session) {
		handlerSessions.push(session);
	}
	
	public synchronized void saveHandler(@Nonnull ResumableServiceHandler<T, V> handler){
		resumableHandlers.push(handler);
	}
	
	public synchronized HandlerSession fetchHandlerSession(){
		return handlerSessions.peek();
	}
	
	public synchronized void removeHandlerSession(){
		handlerSessions.pop();
	}
	
	public synchronized void removeHandler(){
		resumableHandlers.pop();
	}
}
