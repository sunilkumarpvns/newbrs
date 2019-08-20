package com.elitecore.netvertex.core.roaming;

import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingType;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import java.util.Map;
import java.util.Set;

public abstract class RoutingEntry implements ToStringable{
	
	protected String name;
	protected RoutingActions routingAction;
	protected Map<String, Integer> gatewayWeightageMap;
	protected boolean isRoaming;

	public RoutingEntry(String name, RoutingActions action, Map<String, Integer> gatewayWeightageMap, boolean isRoaming) {
		this.name = name;
		this.routingAction = action;
		this.gatewayWeightageMap = gatewayWeightageMap;
		this.isRoaming = isRoaming;
	}

	public RoutingActions getRoutingAction() {
		return routingAction;
	}

	public Set<String> getGatewayNames() {
		return gatewayWeightageMap.keySet();
	}
	
	public int getGatewayWeightage(String hostIdentity){
		Integer val = gatewayWeightageMap.get(hostIdentity);
		if(val != null){
			return val;
		}
		
		return -1;
	}
	
	public boolean isRoaming() {
		return true;
	}
	
	public String getName() {
		return name;
	}
	public abstract String getExpression();

	public abstract RoutingType getType();
}
