package com.elitecore.diameterapi.core.common.session;

import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;

/**
 * 
 * @author pulindani
 *
 */
public interface IApplicationListener {

	public ApplicationEnum[] getApplicationEnum();
	
	public void init() throws AppListenerInitializationFaildException;
	
	public void handleApplicationRequest(Session session, DiameterRequest diameterRequest);
}
