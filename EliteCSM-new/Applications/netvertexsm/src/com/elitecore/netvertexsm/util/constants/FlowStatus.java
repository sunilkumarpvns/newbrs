package com.elitecore.netvertexsm.util.constants;


public enum FlowStatus {
	
	ENABLE_UPLINK(0,"Enabled Uplink"),
	ENABLE_DOWNLINK(1,"Enabled Downlink"),
	ENABLED(2,"Enabled"),
	DISABLED(3,"Disabled"),
	REMOVED(4,"Removed");
	
	public final Integer flowStatusId;
	public final String flowStatus;
	FlowStatus(Integer flowStatusId,String flowStatus){
		this.flowStatusId = flowStatusId;
		this.flowStatus = flowStatus;
	}
	public static String fromFlowStauts(Integer flowStatusId){
		for(FlowStatus status : values()){
			if(status.flowStatusId.intValue() == flowStatusId){
				return status.flowStatus;
			}
		}
		return null;
	}
	public static Integer fromFlowStatusValue(String flowStatus){
		for(FlowStatus status : values()){
			if(status.flowStatus.equalsIgnoreCase(flowStatus)){
				return status.flowStatusId;
			}
		}
		return null;
	}

}
