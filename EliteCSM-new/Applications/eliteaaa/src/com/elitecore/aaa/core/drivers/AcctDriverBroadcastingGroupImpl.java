package com.elitecore.aaa.core.drivers;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.ServiceContext;

public class AcctDriverBroadcastingGroupImpl implements AcctDriverBroadcastingGroup{
	
	private final String MODULE = "ACCT_BROADCASTING";
	private ServiceContext serviceContext;
	private List<IEliteAcctDriver> acctDrivers;

	public  AcctDriverBroadcastingGroupImpl(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
		this.acctDrivers = new ArrayList<IEliteAcctDriver>();
	}
	@Override
	public void addAccountingDriver(IEliteAcctDriver acctDriver) {
		acctDrivers.add(acctDriver);
	}
	@Override
	public void handleAcctRequest(RadAcctRequest radAcctRequest) {
		if(acctDrivers!=null && acctDrivers.size()>0){
			int numOfDriver = acctDrivers.size();
			for(int index = 0; index<numOfDriver; index++) {
			IEliteAcctDriver acctDriver = acctDrivers.get(index);
			
			if(acctDriver != null) {
				try {
					acctDriver.handleAccountingRequest(radAcctRequest);
				}catch(Exception exception) {
					LogManager.getLogger().warn(MODULE, "Driver Processing Fail for " + acctDriver.toString());
					LogManager.getLogger().trace(exception);
				}
			}
		}
	}
	}
	
	

}
