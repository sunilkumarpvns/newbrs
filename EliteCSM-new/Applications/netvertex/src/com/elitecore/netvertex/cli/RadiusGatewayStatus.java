package com.elitecore.netvertex.cli;

import java.util.Map;

import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

public interface RadiusGatewayStatus {
	
	public Map<String, Boolean> getGatewaysState();	
 	
}
