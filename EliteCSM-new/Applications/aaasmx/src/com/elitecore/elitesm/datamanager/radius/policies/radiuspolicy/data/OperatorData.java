package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class OperatorData extends BaseData implements IOperatorData {
	private String operatorId;
	private String operatorName;
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
}
