package com.elitecore.elitesm.web.diameter.routingconfig.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class DiameterRoutingTableForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String routingTableId;
    private String routingTableName;
    private String actionType;
    private String overloadAction;
    private int resultCode;
    private String routingScript;
    
	public String getRoutingTableId() {
		return routingTableId;
	}
	public void setRoutingTableId(String routingTableId) {
		this.routingTableId = routingTableId;
	}
	public String getRoutingTableName() {
		return routingTableName;
	}
	public void setRoutingTableName(String routingTableName) {
		this.routingTableName = routingTableName;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getOverloadAction() {
		return overloadAction;
	}
	public void setOverloadAction(String overloadAction) {
		this.overloadAction = overloadAction;
	}
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getRoutingScript() {
		return routingScript;
	}
	public void setRoutingScript(String routingScript) {
		this.routingScript = routingScript;
	}
	
	
}