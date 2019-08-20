package com.elitecore.core.servicex.base;

import com.elitecore.core.servicex.UDPServiceResponse;

public abstract class BaseUDPServiceResponse extends BaseServiceResponse
		implements UDPServiceResponse {

	public abstract byte[] getResponseBytes();
}
