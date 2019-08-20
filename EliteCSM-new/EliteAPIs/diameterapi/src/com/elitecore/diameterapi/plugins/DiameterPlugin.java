package com.elitecore.diameterapi.plugins;

import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public interface DiameterPlugin {
	public void handleOutMessage(DiameterPacket diameterRequest,DiameterPacket diameterAnswer, 
			ISession session, String argument, PluginCallerIdentity callerID);
	public void handleInMessage(DiameterPacket diameterRequest,DiameterPacket diameterAnswer, 
			ISession session, String argument, PluginCallerIdentity callerID);
	public String getName();
}
