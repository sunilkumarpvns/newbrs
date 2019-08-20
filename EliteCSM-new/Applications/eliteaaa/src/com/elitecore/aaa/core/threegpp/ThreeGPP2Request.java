package com.elitecore.aaa.core.threegpp;

import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

public interface ThreeGPP2Request {
	
	public void handleRequest(ServiceRequest request,ServiceResponse response);
	public boolean isEligible(ServiceRequest request,ServiceResponse response); 


}
