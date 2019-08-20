package com.elitecore.aaa.rm.service.concurrentloginservice;

import com.elitecore.aaa.radius.service.RadServiceRequest;

public interface RadConcLoginRequest extends RadServiceRequest {

	public String getClientIP();
}
