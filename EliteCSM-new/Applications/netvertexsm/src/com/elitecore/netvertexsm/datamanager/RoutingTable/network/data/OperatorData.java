package com.elitecore.netvertexsm.datamanager.RoutingTable.network.data;

import com.elitecore.netvertexsm.web.core.base.BaseData;

public class OperatorData extends BaseData {
	private Long operatorID;
	private String name;
	
	public Long getOperatorID() {
		return operatorID;
	}
	
	public void setOperatorID(Long operatorID) {
		this.operatorID = operatorID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
