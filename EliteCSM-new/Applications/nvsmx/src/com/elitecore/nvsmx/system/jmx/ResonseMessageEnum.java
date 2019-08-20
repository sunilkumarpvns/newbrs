package com.elitecore.nvsmx.system.jmx;

public enum ResonseMessageEnum {

	SUCCESS("SUCCESS"),
	FAIL("FAIL"),
	MIGRATION_IS_IN_PROGRESS("MIGRATION IS IN PROGRESS"),
	MIGRATION_IS_STARTED("MIGRATION IS STARTED"),
	NO_TASK_RUNNING("NO TASK RUNNING")
	
	;
	
	
	public String messsage;
	private ResonseMessageEnum(String message) {
		this.messsage = message;
	}
}
