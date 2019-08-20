package com.elitecore.diameterapi.diameter.common.data;

import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;

public interface DiameterFailoverConfiguration {

	public DiameterFailureConstants getFailoverAction();
	public String getFailoverArguments();
	public String getErrorCodes();
}
