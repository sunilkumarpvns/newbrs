package com.elitecore.netvertex.cli;

import java.util.Map;

import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

public interface DiameterGatewayStatus {
	
	public Map<String, IStateEnum> getGatewaysState();	
	public boolean closeGateway(String hostIdentity);
	public boolean forceCloseGateway(String hostIdentity);
	public boolean startGateway(String hostIdentity);
	
}
