package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.core.services.handler.ServiceHandler;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.commons.ReInitializable;

public interface DiameterApplicationHandler<T extends ApplicationRequest, V extends ApplicationResponse> extends ServiceHandler<T, V>, ReInitializable {
	
}
