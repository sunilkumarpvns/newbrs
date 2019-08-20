package com.elitecore.diameterapi.diameter.common.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public interface RealmData {
	
	public final static short STATEFUL_ROUTING_ENABLED = 1;
	public final static short STATEFUL_ROUTING_DISABLED = 0;
	
	public String getDestRealm();
	public RoutingActions getRoutinAction();
	public String getRoutingName();
	public Set<ApplicationEnum> getApplications();
	public List<PeerGroupImpl> getPeerGroupList();
	public LogicalExpression getAdvancedCondition();
	public String getAdvancedConditionStr();
	public String getOriginatorHostIp();
	public String getTransMapName();
	public boolean getAttachedRedirection();
	
	// Added For Failover Scenario.
	public long getTransActionTimeOut();
	public List<DiameterFailoverConfigurationImpl> getFailoverDataList(); 
	public Map<Integer, DiameterFailoverConfiguration> getFailoverDataMap(); 
	public short getStatefulRouting();
	public String getOriginRealm();
}

