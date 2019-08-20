package com.elitecore.diameterapi.core.common.transport.tcp.collections;

import com.elitecore.diameterapi.core.common.transport.tcp.collections.WeightedFairBlockingQueue.Range;

/**
 * 
 * Interface to customize Priority of Elements 
 * 
 * @author monica.lulla
 *
 * @param <E>
 */
public interface FairnessPolicy<E> {

	/**
	 * 
	 * 
	 * @param e
	 * @return
	 */
	int prioritize(E e);
	
	Range range();
}
