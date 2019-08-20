package com.elitecore.elitesm.web.radius.policies.accesspolicy.forms;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class ViewAccessPolicyForm extends BaseDictionaryForm{
	
	private String name;
	private String description;
	private String status;
	private String action;
	private String accessPolicyId;
	private String assigned;
	private List lstAccessPolicyDetail;
	private Timestamp lastUpdated;
    private Set accessPolicyDetailDataSet;
	private String systemGenerated;
	private String accessStatus;
	
	private String serialNumber;
	private Date startTime;
	private Date stopTime;
	private String weekDayId;
	
	private String startWeekDay;
	private String endWeekDay;
	private String remove;
	private String weekDay; 
	
	
	
	public String getAccessStatus() {
		return accessStatus;
	}
	public void setAccessStatus(String accessStatus) {
		this.accessStatus = accessStatus;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Set getAccessPolicyDetailDataSet() {
		return accessPolicyDetailDataSet;
	}
	public void setAccessPolicyDetailDataSet(Set accessPolicyDetailDataSet) {
		this.accessPolicyDetailDataSet = accessPolicyDetailDataSet;
	}
	public String getAccessPolicyId() {
		return accessPolicyId;
	}
	public void setAccessPolicyId(String accessPolicyId) {
		this.accessPolicyId = accessPolicyId;
	}
	public String getAssigned() {
		return assigned;
	}
	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}
	public List getAccessPolicyDetail() {
		return lstAccessPolicyDetail;
	}
	public void setAccessPolicyDetail(List lstAccessPolicyDetail) {
		this.lstAccessPolicyDetail = lstAccessPolicyDetail;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public String getWeekDayId() {
		return weekDayId;
	}
	public void setWeekDayId(String weekDayId) {
		this.weekDayId = weekDayId;
	}
	/**
	 * @return Returns the endWeekDay.
	 */
	public String getEndWeekDay() {
		return endWeekDay;
	}
	/**
	 * @param endWeekDay The endWeekDay to set.
	 */
	public void setEndWeekDay(String endWeekDay) {
		this.endWeekDay = endWeekDay;
	}
	/**
	 * @return Returns the lstAccessPolicyDetail.
	 */
	public List getLstAccessPolicyDetail() {
		return lstAccessPolicyDetail;
	}
	/**
	 * @param lstAccessPolicyDetail The lstAccessPolicyDetail to set.
	 */
	public void setLstAccessPolicyDetail(List lstAccessPolicyDetail) {
		this.lstAccessPolicyDetail = lstAccessPolicyDetail;
	}
	/**
	 * @return Returns the remove.
	 */
	public String getRemove() {
		return remove;
	}
	/**
	 * @param remove The remove to set.
	 */
	public void setRemove(String remove) {
		this.remove = remove;
	}
	/**
	 * @return Returns the startWeekDay.
	 */
	public String getStartWeekDay() {
		return startWeekDay;
	}
	/**
	 * @param startWeekDay The startWeekDay to set.
	 */
	public void setStartWeekDay(String startWeekDay) {
		this.startWeekDay = startWeekDay;
	}
	/**
	 * @return Returns the startTime.
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime The startTime to set.
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return Returns the stopTime.
	 */
	public Date getStopTime() {
		return stopTime;
	}
	/**
	 * @param stopTime The stopTime to set.
	 */
	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}
	public String getWeekDay() {
		return weekDay;
	}
	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}
	
}
