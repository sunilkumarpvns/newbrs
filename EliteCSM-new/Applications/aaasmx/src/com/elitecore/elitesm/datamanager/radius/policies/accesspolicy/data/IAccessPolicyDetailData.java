package com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data;

import java.sql.Timestamp;
import java.util.List;


public interface IAccessPolicyDetailData {
	public String getAccessPolicyId();
	public void setAccessPolicyId(String accessPolicyId);
	public String getAccessStatus();
	public void setAccessStatus(String accessStatus); 
	public String getEndWeekDay();
	public void setEndWeekDay(String endWeekDay);
	public long getSerialNumber();
	public void setSerialNumber(long serialNumber);
	public String getStartWeekDay();
	public void setStartWeekDay(String startWeekDay); 
	public Timestamp getStartTime();
	public void setStartTime(Timestamp startTime);
	public Timestamp getStopTime();
	public void setStopTime(Timestamp stopTime);
	public List getLstWeeKDay();
	public void setLstWeeKDay(List lstWeeKDay);
}
