package com.elitecore.corenetvertex.constants;

public enum VolumeUsageType {

	TOTAL_OCTETS("TOTAL"),
	DOWNLOAD_OCTETS("DOWNLOAD"),
	UPLOAD_OCTETS("UPLOAD");
	
	public final String val;
	private VolumeUsageType(String volumeUsageType){
		this.val = volumeUsageType;
	}
	
	public static final VolumeUsageType fromValue(String value){
		if(value == null){	
			return null;
		}
		if(value.equalsIgnoreCase(DOWNLOAD_OCTETS.val)){
			return DOWNLOAD_OCTETS;
		}else if(value.equalsIgnoreCase(UPLOAD_OCTETS.val)){
			return UPLOAD_OCTETS;
		}else if(value.equalsIgnoreCase(TOTAL_OCTETS.val)){
			return TOTAL_OCTETS;
		}else{
			return null;
		}
	}
}
