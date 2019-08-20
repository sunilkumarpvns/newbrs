package com.elitecore.core.servicex;

public interface AsyncRequestExecutor<T extends ServiceRequest, V extends ServiceResponse> {
	void handleServiceRequest(T serviceRequest, V serviceResponse);
}
