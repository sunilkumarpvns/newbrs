/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.radius.service.acct;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.acct.policy.AcctServicePolicy;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;


/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public interface RadAcctRequest extends RadServiceRequest {
	public AcctServicePolicy getServicePolicy();

	public RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> getExecutor();
	public void setExecutor(RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> executor);
}
