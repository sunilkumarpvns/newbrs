package com.elitecore.aaa.core.drivers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;

public interface AcctDriverBroadcastingGroup {
	public void addAccountingDriver(IEliteAcctDriver acctDriver);
	
	public void handleAcctRequest(RadAcctRequest radAcctRequest);
	
}
