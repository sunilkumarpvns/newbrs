package com.elitecore.corenetvertex.constants;


public enum ServicePolicyActions {

	PROCESSREQUEST(1, "Process Request"),
	DROPREQUEST(2, "Drop Request"),
	;
	
	private int id;
	private String name;
	
	private ServicePolicyActions(int val, String name) {
		this.id = val;
		this.name = name;
	}
	
	public static ServicePolicyActions fromValue(int action){
		if (PROCESSREQUEST.id == action) {
			return PROCESSREQUEST;
		} else if (DROPREQUEST.id == action) {
			return DROPREQUEST;
		}
		return PROCESSREQUEST;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
