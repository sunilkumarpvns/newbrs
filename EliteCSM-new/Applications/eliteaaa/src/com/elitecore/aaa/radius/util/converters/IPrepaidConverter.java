package com.elitecore.aaa.radius.util.converters;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;

public interface IPrepaidConverter {
	
	public void convertToStandard(RadAuthResponse response) throws Exception;
	public void convertFromStandard(RadAuthRequest request) throws Exception;
	
}
