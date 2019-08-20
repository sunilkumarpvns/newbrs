package com.elitecore.aaa.radius.service.handlers;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.services.handler.ServiceHandler;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

/**
 * Represents a sequence of handlers and provides support framework for specific 
 * chain handlers.
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
 * @param <T> type of request packet
 * @param <V> type of response packet
 */
public abstract class ChainHandler<T extends ServiceRequest, V extends ServiceResponse, H extends ServiceHandler<T, V>> 
implements ServiceHandler<T, V>, ResumableServiceHandler<T, V>, Iterable<H> {

	private List<H> handlers = new ArrayList<H>();
	
	/**
	 * Add a new handler in the ordered sequence of handlers. This method provides 
	 * fluent interface so that the operations can be chained like
	 * <code>chainHandler.addHandler(handler1).addHandler(handler2);</code>
	 * 
	 * @param handler a non-null handler
	 * @return this
	 */
	public ChainHandler<T, V, H> addHandler(@Nonnull H handler){
		this.handlers.add(checkNotNull(handler, "handler is null"));
		return this;
	}
	
	/**
	 * Number of the handlers in chain
	 * 
	 * @return number of handlers
	 */
	public int size() {
		return handlers.size();
	}
	
	/**
	 * A immutable iterator, removing is not supported and throws {@code UnsupportedOperationException}
	 * if called.
	 * 
	 */
	@Override
	public Iterator<H> iterator() {
		return new ServiceHandlerIterator();
	}
	
	@Override
	public boolean isResponseBehaviorApplicable() {
		Iterator<H> iterator = iterator();
		while(iterator.hasNext()) {
			if(iterator.next().isResponseBehaviorApplicable()) {
				return true;
			}
		}
		
		return false;
	}
	
	class ServiceHandlerIterator implements Iterator<H> {
		
		private Iterator<H> iterator;

		public ServiceHandlerIterator() {
			iterator = handlers.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public H next() {
			return iterator.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Handlers cannot be removed once added in chain");
		}
	}
}
