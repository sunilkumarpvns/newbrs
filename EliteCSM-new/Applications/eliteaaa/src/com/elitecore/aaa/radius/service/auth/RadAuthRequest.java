/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.radius.service.auth;

import com.elitecore.aaa.radius.policies.servicepolicy.RadiusServicePolicy;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;

/**
 * 
 * @author baiju
 *
 */
public interface RadAuthRequest extends RadServiceRequest{
	
	public IEapStateMachine getEapSession();
	public RadiusServicePolicy<RadAuthRequest, RadAuthResponse> getServicePolicy();
	public RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> getExecutor();
	public void setExecutor(RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor);
}
