package com.elitecore.aaa.core.drivers;

import java.util.List;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.systemx.esix.ESCommunicator;

public interface IEliteAuthDriver extends ESCommunicator {
	
	public AccountData getAccountData(ServiceRequest serviceRequest, List<String> userIdentities, ChangeCaseStrategy caseStrategy,boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator) throws DriverProcessFailedException;

	public AccountData getAccountData(ServiceRequest serviceRequest, ChangeCaseStrategy caseStrategy, boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String userIdentity)throws DriverProcessFailedException;
	
	public String getDriverInstanceId();
	
	public void saveAccountData(AccountData accountData);
}