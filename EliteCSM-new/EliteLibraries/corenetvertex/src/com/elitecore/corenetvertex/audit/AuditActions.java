package com.elitecore.corenetvertex.audit;

public enum AuditActions {

	CREATE(0),
	VIEW(1),
	UPDATE(2),
	DELETE(3)
	;
	
	private int actionCode;
	private AuditActions(int actionCode){
		this.actionCode= actionCode;
	}

	public int getVal() {
		return actionCode;
	}
	
}
