package com.elitecore.diameterapi.mibs.statistics;


public class ApplicationStatsIdentifier {
	private long  applicationId;
	private long vendorId;
	private String application;
	private int hash = -1;
	private String appIdentifierString;
	
	public ApplicationStatsIdentifier(long appId, long vendorId, String application){
		
		this.applicationId = appId;
		this.vendorId = vendorId;
		this.application = application.toLowerCase();
		this.appIdentifierString = "Application: "+ this.application.toUpperCase() +
				" (" + this.vendorId +":" + this.applicationId + ")";
	}
	
	public String getApplication() {
		return this.application; 
	}
	
	public long getApplicationId() {
		return this.applicationId; 
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null)
			return false;
		
		if(obj == this)
			return true;
		try{
			ApplicationStatsIdentifier applicationStatsIdentifier = (ApplicationStatsIdentifier) obj;
			
			if(this.applicationId == applicationStatsIdentifier.applicationId &&
					this.vendorId == applicationStatsIdentifier.vendorId )
					return true;
		}catch(ClassCastException e){}
		return false;
	}
	
	@Override
	public int hashCode() {

		if(hash == -1){
			hash = 53 + (int)this.applicationId;
			hash = 53*hash + (int)this.vendorId;
			if(hash < 0){
				return hash + Integer.MAX_VALUE;
			}
		}
		return hash;
	}
	
	@Override
	public String toString() {
		return appIdentifierString;
	}
}
