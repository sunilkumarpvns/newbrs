package com.elitecore.corenetvertex.constants;

public enum CommonStatus {
	
	ACTIVE("CST01","Active"),
	INACTIVE("CST02","InActive"),
	DEFAULT("CST00","Default"),
	DELETE("CST03","Delete");
	
	public final String statusId;
	public final String statusName;
	
	private CommonStatus(String statusId,String statusName){
		this.statusId = statusId;
		this.statusName = statusName;
	}
	
	public static CommonStatus fromStatusName(String statusName){
		if(statusName.equalsIgnoreCase(ACTIVE.statusName)){
			return ACTIVE;
		}else if(statusName.equalsIgnoreCase(INACTIVE.statusName)){
			return INACTIVE;
		}else if(statusName.equalsIgnoreCase(DEFAULT.statusName)){
			return DEFAULT;
		}else if(statusName.equalsIgnoreCase(DELETE.statusName)){
			return DELETE;
		}else{
			return null;
		}
	}
	
	
	public static CommonStatus fromStatusId(String statusId){
		if(statusId.equalsIgnoreCase(ACTIVE.statusId)){
			return ACTIVE;
		}else if(statusId.equalsIgnoreCase(INACTIVE.statusId)){
			return INACTIVE;
		}else if(statusId.equalsIgnoreCase(DEFAULT.statusId)){
			return DEFAULT;
		}else if(statusId.equalsIgnoreCase(DELETE.statusId)){
			return DELETE;
		}else{
			return null;
		}
	}

}
