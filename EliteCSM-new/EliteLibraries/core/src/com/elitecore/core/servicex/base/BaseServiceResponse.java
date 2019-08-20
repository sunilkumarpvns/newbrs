package com.elitecore.core.servicex.base;

import com.elitecore.core.servicex.ServiceResponse;

/**
 * 
 * @author Elitecore Technologies
 *
 */
public abstract class BaseServiceResponse implements ServiceResponse {
	
	private boolean markedForDropRequest;
	private boolean processingCompleted=true;
	
	public boolean isMarkedForDropRequest() {
		return markedForDropRequest;
	}

	public void markForDropRequest() {
		markedForDropRequest = true;
	}
	
	@Override
	public boolean isProcessingCompleted() {
		return processingCompleted;
	}
	@Override
	public void setProcessingCompleted(boolean value) {
		processingCompleted = value;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
