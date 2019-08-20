/*
 * 
 */

package com.elitecore.core.servicex.base;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.servicex.ServiceRequest;


/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public abstract class BaseServiceRequest implements ServiceRequest {
	private final long requestReceivedNano;
	public BaseServiceRequest() {
		super();
		requestReceivedNano = System.nanoTime();
	}
	
	public BaseServiceRequest(TimeSource timeSource) {
        super();
        requestReceivedNano = timeSource.currentTimeInMillis();
    }

	public long getRequestReceivedNano() {
		return this.requestReceivedNano;
	}
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
