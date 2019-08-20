package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;

/**
 * 
 * @author narendra.pathai
 *
 */
public interface AcctServicePolicyHandlerData extends ServicePolicyHandlerData {
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext);
}
