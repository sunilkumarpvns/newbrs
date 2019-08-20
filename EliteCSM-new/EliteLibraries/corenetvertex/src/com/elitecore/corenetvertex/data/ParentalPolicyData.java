package com.elitecore.corenetvertex.data;


public class ParentalPolicyData  {

	private String daysOfTheWeek;
	private String timePeriod;
	private Long addOnPackageId;
	
	public String getDaysOfTheWeek() {
		return daysOfTheWeek;
	}
	public void setDaysOfTheWeek(String daysOfTheWeek) {
		this.daysOfTheWeek = daysOfTheWeek;
	}
	
	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}
	public String getTimePeriod() {
		return timePeriod;
	}
	public Long getAddOnPackageId() {
		return addOnPackageId;
	}
	public void setAddOnPackageId(Long addOnPackageId) {
		this.addOnPackageId = addOnPackageId;
	}
	
	public String toString(){
		
	    StringBuilder out = new StringBuilder();
	    out.append(getDaysOfTheWeek()+"|"+getTimePeriod()+"|"+getAddOnPackageId());
	    
	    return	out.toString();


	}
	
}
