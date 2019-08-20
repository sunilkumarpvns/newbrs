package com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data;

import java.util.List;
import java.util.Set;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;


public class RoutingTableData {

	private long routingTableId;
	private String name;
	private String type;
	private long routingAction;
	private Long mccmncGroupId;
	private String roaming;
	private Integer orderNumber;
	private String realmCondition;
	private MCCMNCGroupData mccmncGroupData;
	private GatewayData gatewayData;
	private List<RoutingTableGatewayRelData> routingTableGatewayRelData;
	private Set<RoutingTableGatewayRelData> routingTableGatewayRelSet;
	
	
	public long getRoutingTableId() {
		return routingTableId;
	}
	public void setRoutingTableId(long routingTableId) {
		this.routingTableId = routingTableId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getRoutingAction() {
		return routingAction;
	}
	public void setRoutingAction(long routingAction) {
		this.routingAction = routingAction;
	}
	public Long getMccmncGroupId() {
		return mccmncGroupId;
	}
	public void setMccmncGroupId(Long mccmncGroupId) {
		this.mccmncGroupId = mccmncGroupId;
	}
	public String getRoaming() {
		return roaming;
	}
	public void setRoaming(String roaming) {
		this.roaming = roaming;
	}
	public String getRealmCondition() {
		return realmCondition;
	}
	public void setRealmCondition(String realmConndtion) {
		this.realmCondition = realmConndtion;
	}
	public List<RoutingTableGatewayRelData> getRoutingTableGatewayRelData() {
		return routingTableGatewayRelData;
	}
	public void setRoutingTableGatewayRelData(
			List<RoutingTableGatewayRelData> routingTableGatewayRelData) {
		this.routingTableGatewayRelData = routingTableGatewayRelData;
	}
	public MCCMNCGroupData getMccmncGroupData() {
		return mccmncGroupData;
	}
	public void setMccmncGroupData(MCCMNCGroupData mccmncGroupData) {
		this.mccmncGroupData = mccmncGroupData;
	}
	public Set<RoutingTableGatewayRelData> getRoutingTableGatewayRelSet() {
		return routingTableGatewayRelSet;
	}
	public void setRoutingTableGatewayRelSet(Set<RoutingTableGatewayRelData> routingTableGatewayRelSet) {
		this.routingTableGatewayRelSet = routingTableGatewayRelSet;
	}
	public GatewayData getGatewayData() {
		return gatewayData;
	}
	public void setGatewayData(GatewayData gatewayData) {
		this.gatewayData = gatewayData;
	}
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	
}
