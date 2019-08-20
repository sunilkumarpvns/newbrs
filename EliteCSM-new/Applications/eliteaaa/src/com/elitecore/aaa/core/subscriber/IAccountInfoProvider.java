package com.elitecore.aaa.core.subscriber;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

public interface IAccountInfoProvider<T extends ServiceRequest, V extends ServiceResponse> {
	public AccountData getAccountData(T request, V serviceResponse);
	public AccountData getAccountData(T serviceRequest, V serviceResponse, String userIdentity);
	
}