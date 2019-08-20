package com.elitecore.elitesm.web.radius.policies.accesspolicy.forms;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyDetailData;
import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class EditAccessPolicyForm extends BaseDictionaryForm{
	
	private String name;
	private String accessPolicyId;
	private String description;
	private String assigned;
	private boolean allowed;
	private boolean denied;
	private boolean show;
	private Timestamp lastUpdated;
	private String commonStatusId;
	private String action;
	private String status;
	private String accessstatus;
	private FormFile fileUpload;
	private String upoadFileName;
	private int itemIndex;
	//private Set accessPolicyDetailDataSet;
	private List lstWeeKDay = new ArrayList();
	
	public boolean isAllowed() {
		return allowed;
	}
	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}
	public boolean isDenied() {
		return denied;
	}
	public void setDenied(boolean denied) {
		this.denied = denied;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public FormFile getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(FormFile fileUpload) {
		this.fileUpload = fileUpload;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getAccessPolicyId() {
		return accessPolicyId;
	}
	public void setAccessPolicyId(String accessPolicyId) {
		this.accessPolicyId = accessPolicyId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUpoadFileName() {
		return upoadFileName;
	}
	public void setUpoadFileName(String upoadFileName) {
		this.upoadFileName = upoadFileName;
	}
	public String getAssigned() {
		return assigned;
	}
	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}
	public Timestamp getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public String getAccessstatus() {
		return accessstatus;
	}
	public void setAccessstatus(String accessstatus) {
		this.accessstatus = accessstatus;
	}
	
	
	public long getSerialNumber(int index) {
		
		return ((AccessPolicyDetailData)lstWeeKDay.get(index)).getSerialNumber();
		
	}
	
	public void setSerialNumber(int index,long serialNumber){
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setSerialNumber(serialNumber);
	}
	
	public String getStartWeekDay(int index) {
		return ((AccessPolicyDetailData)lstWeeKDay.get(index)).getStartWeekDay();
	}
	
	public void setStartWeekDay(int index,String startWeekDay){
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setStartWeekDay(startWeekDay);
	}
	
	public void setStartHours(int index, int startHours){
		GregorianCalendar gCal = new GregorianCalendar();
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		int minutes = getMinutes(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStartTime());
		if(minutes <= 0)minutes = 0;
		gCal.set(1700,1,1,startHours,minutes,0);
		java.sql.Timestamp startTime = new java.sql.Timestamp(gCal.getTimeInMillis());
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setStartTime(startTime);
	}
	
	public int getStartHours(int index){
		int hours = getHours(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStartTime());
		return hours;
	}
	
	public void setStartMinutes(int index, int startMinutes){
		GregorianCalendar gCal = new GregorianCalendar();
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		int hours = getHours(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStartTime());
		if(hours <= 0)hours = 0;
		gCal.set(1700,1,1,hours,startMinutes,0);
		java.sql.Timestamp startTime = new java.sql.Timestamp(gCal.getTimeInMillis());
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setStartTime(startTime);
	}	
	
	public int getStartMinutes(int index){
		int hours = getMinutes(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStartTime());
		return hours;
	}
	
	public void setStopHours(int index, int stopHours){
		GregorianCalendar gCal = new GregorianCalendar();
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		int minutes = getMinutes(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStopTime());
		if(minutes <= 0)minutes = 0;
		gCal.set(1700,1,1,stopHours,minutes,0);
		java.sql.Timestamp stopTime = new java.sql.Timestamp(gCal.getTimeInMillis());
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setStopTime(stopTime);
	}		 
	
	public int getStopHours(int index){
		
		int hours = getHours(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStopTime());
		return hours;
	}
	
	public void setStopMinutes(int index, int stopMinutes){
		GregorianCalendar gCal = new GregorianCalendar();
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		int hours = getHours(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStopTime());
		if(hours <= 0)hours = 0;
		gCal.set(1700,1,1,hours,stopMinutes,0);
		java.sql.Timestamp stopTime = new java.sql.Timestamp(gCal.getTimeInMillis());
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setStopTime(stopTime);
	}		 	
	
	public int getStopMinutes(int index){
		int minutes = getMinutes(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStopTime());
		return minutes;
	}
	
	public String getEndWeekDay(int index) {
		return ((AccessPolicyDetailData)lstWeeKDay.get(index)).getEndWeekDay();
	}
	
	public void setEndWeekDay(int index,String endWeekDay) {
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setEndWeekDay(endWeekDay);
	}
	
	public String getAccessStatus(int index) {
		return ((AccessPolicyDetailData)lstWeeKDay.get(index)).getAccessStatus();
	}
	
	public void setAccessStatus(int index,String accessStatus) {
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setAccessStatus(accessStatus);
	}
	
	public List getLstWeeKDay() {
		return lstWeeKDay;
	}
	
	public void setLstWeeKDay(List lstWeeKDay) {
		this.lstWeeKDay = lstWeeKDay;
	}
	
	private int getMinutes(Timestamp timestamp){
		int minutes=-1;
		if(timestamp != null){		
			Calendar newCal = new GregorianCalendar();
			newCal.setTime(timestamp);
			minutes = newCal.get( Calendar.MINUTE ) ;
		}
		return minutes;
	}
	
	private int getHours(Timestamp timestamp){
		int hours=0;
		if(timestamp != null){
			Calendar newCal = new GregorianCalendar();
			newCal.setTime(timestamp);
			hours = newCal.get( Calendar.HOUR_OF_DAY ) ;
		}
		return hours;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	
}
