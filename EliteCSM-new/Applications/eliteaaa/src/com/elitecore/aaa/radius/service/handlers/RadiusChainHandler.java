package com.elitecore.aaa.radius.service.handlers;

import java.util.Iterator;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * 
 * Represents a sequence of handlers that need to be applied in order.
 * Before calling each handler the eligibility of the handler is checked, only if 
 * the handler is eligible it is called else that handler is skipped. 
 * <br/><br/>
 * Any handler can break the sequence of execution either by rejecting the request, dropping 
 * the request. In that case no further handlers are applied.
 * <br/><br/> 
 * When any handler performs some asynchronous communication the chain suspends the further
 * execution of the handlers, which is resumed when the request is put back in the request
 * handling queue. To override the way in which the chain decides whether further processing
 * should be continued or not, {@link ProcessingStrategy} can be provided.
 * 
 * <pre>
 * +---------------------------------------------------+
 * |      +-------+     +-------+          +-------+   |
 * |      |       |     |       |          |       |   |
 * | +---->   1   +----->   2   |   . . .  |   N   |   |
 * |      |       |     |       |          |       |   |
 * |      +-------+     +-------+          +-------+   |
 * +---------------------------------------------------+
 * </pre>
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request
 * @param <V> type of response
 */
public abstract class RadiusChainHandler<T extends RadServiceRequest,
V extends RadServiceResponse, H extends RadServiceHandler<T, V>> 
extends ChainHandler<T, V, H> implements RadServiceHandler<T, V> {
	
	private static final String ITERATOR = "ITERATOR";
	private final ProcessingStrategy processingStrategy;

	public RadiusChainHandler(ProcessingStrategy processingStrategy) {
		this.processingStrategy = processingStrategy;
	}

	public RadiusChainHandler() {
		this(new DefaultProcessingStrategy());
	}

	public boolean isEligible(T request, V response) {
		return true;
	}

	public void handleRequest(T request, V response, ISession session) {
		// if the processing is already completed then no point in handling request
		if (shouldContinue(request, response) == false) {
			return;
		}
		
		Iterator<H> iterator = iterator();
		saveSession(request, iterator);
		process(request, response, iterator, session);
		
		/* After handling if processing is stalled due to external communication
		 * then the handler process is not yet complete so should not remove session.
		 * But if processing is completed then session must be removed.
		 */
		if (shouldContinue(request, response)) {
			removeHandlerSession(request);
		}
	}

	protected void saveSession(T request, Iterator<H> iterator) {
		getExecutor(request).saveHandler(this);
		getExecutor(request).saveHandlerSession(createSession(iterator));
	}

	private void removeHandlerSession(T request) {
		getExecutor(request).removeHandlerSession();
		getExecutor(request).removeHandler();
	}

	private void process(T request, V response, Iterator<H> iterator, ISession session) {
		while (iterator.hasNext()) {
			H next = iterator.next();
			if (next.isEligible(request, response) == false) {
				continue;
			}
			next.handleRequest(request, response, session);
			if (shouldContinue(request, response) == false) {
				break;
			}
		}
	}

	private HandlerSession createSession(Iterator<H> iterator) {
		return new HandlerSession().put(ITERATOR, iterator);
	}

	@Override
	public void resumeRequest(T request, V response, ISession session) {
		/*
		 * On resumption if further processing not required then just remove 
		 * the session
		 */
		if (shouldContinue(request, response) == false) {
			removeHandlerSession(request);
			return;
		}
		
		Iterator<H> iterator = fetchIteratorFromSession(request);
		process(request, response, iterator, session);
		
		/*
		 * After resumption handling if processing is stalled due to external communication
		 * then the handler process is not yet complete so should not remove session.
		 * But if processing is completed then session must be removed.
		 * 
		 */
		if (shouldContinue(request, response)) {
			removeHandlerSession(request);
		}
	}


	protected boolean shouldContinue(T request, V response) {
		return processingStrategy.shouldContinue(request, response);
	}

	private Iterator<H> fetchIteratorFromSession(T request) {
		return getExecutor(request).fetchHandlerSession().get(ITERATOR);
	}

	protected abstract RadiusRequestExecutor<T, V> getExecutor(T request);

	/**
	 * Represents the decision whether the next handler in the chain should be executed
	 * <p>
	 * It is recommended to keep this class stateless, any state that needs to be preserved
	 * can be stored in request parameters if the state is request scope. But implementations
	 * are free to violate this, if need be.
	 * 
	 * @author narendra.pathai
	 *
	 */
	public interface ProcessingStrategy {
		/**
		 * The implementation should decide whether next handler should be called or 
		 * further execution should be stopped.
		 * <p>
		 * This method is called at various points during the execution of the chain
		 * and all the scenarios should be kept in mind while implementing this.
		 * The first time this method is called when the chain starts its execution, to
		 * check whether the request is to be handled. Then after application of each
		 * handler in the chain this method is called to check whether the processing 
		 * should proceed to next handler or not. The same sequence follows when the chain
		 * resumes its processing.
		 * <p>
		 * This method is also called when the chain is completing/halting its execution
		 * to check whether the session of the chain should be removed or preserved for 
		 * resumption.
		 * 
		 * @param request type of request packet
		 * @param response type of response packet
		 * @return true if the next handler should be applied, false otherwise. Chain execution
		 * is stopped when return value is false.
		 */
		public boolean shouldContinue(RadServiceRequest request, RadServiceResponse response); 
	}

	/**
	 * Uses the response flags to decide whether further processing is required.
	 * 
	 * @author narendra.pathai
	 *
	 */
	public static class DefaultProcessingStrategy implements ProcessingStrategy {

		@Override
		public boolean shouldContinue(RadServiceRequest request, RadServiceResponse response) {
			return response.isFurtherProcessingRequired() 
					&& !response.isMarkedForDropRequest();
		}
	}

	/**
	 * Continues the chain processing irrespective of flags set.
	 * 
	 * @author narendra.pathai
	 *
	 */
	public static class ContinueProcessingStrategy implements ProcessingStrategy {

		@Override
		public boolean shouldContinue(RadServiceRequest request, RadServiceResponse response) {
			return true; //will always continue processing no matter what flags are set
		}
	}
}
