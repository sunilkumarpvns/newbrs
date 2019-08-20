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

import java.net.UnknownHostException;

import com.elitecore.aaa.radius.conf.RadAuthConfiguration;
import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.BWMode;
import com.elitecore.core.system.comm.ILocalResponseListener;

/**
 * 
 * @author baiju
 *
 */
public interface RadAuthServiceContext extends RadServiceContext<RadAuthRequest, RadAuthResponse> {

	public RadAuthConfiguration getAuthConfiguration();
	
	public boolean isBlockedUser(RadAuthRequest request,BWMode mode);
	
	public void submitLocalRequest(byte[] requestBytes,ILocalResponseListener responseListener) throws UnknownHostException ;
}
