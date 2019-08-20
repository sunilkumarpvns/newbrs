package com.elitecore.aaa.radius.service.acct.policy.handlers.conf;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerData;

/**
 * 
 * @author narendra.pathai
 *
 */
public interface AcctServicePolicyHandlerData extends ServicePolicyHandlerData {
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext);
}
