package com.elitecore.netvertexsm.util.constants;


public enum UsageMeteringStatus {
	ENABLED("UMT02","Enable"),
	DISABLED("UMT04","Disable");
	public final String usageMeteringStatusId;
	public final String usageMeteringStatusType;
	
	UsageMeteringStatus(String usageMeteringStatusId,String usageMeteringStatusType){
		this.usageMeteringStatusId = usageMeteringStatusId;
		this.usageMeteringStatusType = usageMeteringStatusType;
	}
	public static UsageMeteringStatus fromStatus(String statusId){
		if(statusId.equalsIgnoreCase(ENABLED.usageMeteringStatusId)){
			return ENABLED;
		}else if(statusId.equalsIgnoreCase(DISABLED.usageMeteringStatusId)){
			return DISABLED;
		}
		return null;
	}
	public static String fromStatusType(String status){
		if(status.equalsIgnoreCase(ENABLED.usageMeteringStatusType)){
			return ENABLED.usageMeteringStatusId;
		}else if(status.equalsIgnoreCase(DISABLED.usageMeteringStatusType)){
			return DISABLED.usageMeteringStatusId;
		}
		return null;
	}
}
