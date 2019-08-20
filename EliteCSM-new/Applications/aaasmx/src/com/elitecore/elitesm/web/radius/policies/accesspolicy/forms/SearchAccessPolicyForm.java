package com.elitecore.elitesm.web.radius.policies.accesspolicy.forms;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyDetailData;
import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class SearchAccessPolicyForm extends BaseDictionaryForm{
	
	private String name;
	private String accessPolicyId;
	private String description;
	private String assigned;
	private Timestamp lastUpdated;
	private boolean show;
	private boolean hide;
	private boolean all;
	private boolean allowed;
	private boolean denied;
	private long pageNumber;
	private int multiFactor;
	private long totalPages;
	private long totalRecords;
	private String commonStatusId;
	private String action;
	private String status;
	private String accessstatus;
	private List listAccessPolicy;
	private Collection accessPolicyList;
	private FormFile fileUpload;
	private String upoadFileName;
	
	private List lstWeeKDay = new ArrayList();
	private int itemIndex;
	private String serialNumber;
	private String weekDayId;
	
	
	/*private String uploadFileName;
	 private String uploadedBy;
	 private String uploadedDate;*/
	
	/*	//  File Related Methods Addess  
	 private boolean writeFile;
	 private FormFile theFile;	
	 private String filePath;
	 public String getFilePath() {
	 return filePath;
	 }
	 public void setFilePath(String filePath) {
	 this.filePath = filePath;
	 }
	 public FormFile getTheFile() {
	 return theFile;
	 }
	 public void setTheFile(FormFile theFile) {
	 this.theFile = theFile;
	 }
	 public boolean isWriteFile() {
	 return writeFile;
	 }
	 public void setWriteFile(boolean writeFile) {
	 this.writeFile = writeFile;
	 }
	 // File Related Methods ended here
	  */	
	public Collection getAccessPolicyList() {
		return accessPolicyList;
	}
	public void setAccessPolicyList(Collection accessPolicyList) {
		this.accessPolicyList = accessPolicyList;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean isAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
	}
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public boolean isHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	public List getListAccessPolicy() {
		return listAccessPolicy;
	}
	public void setListAccessPolicy(List listAccessPolicy) {
		this.listAccessPolicy = listAccessPolicy;
	}
	public int getMultiFactor() {
		return multiFactor;
	}
	public void setMultiFactor(int multiFactor) {
		this.multiFactor = multiFactor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
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
	public String getAccessstatus() {
		return accessstatus;
	}
	public void setAccessstatus(String accessstatus) {
		this.accessstatus = accessstatus;
	}
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
	public FormFile getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(FormFile fileUpload) {
		this.fileUpload = fileUpload;
	}
	/*public String getUploadedBy() {
	 return uploadedBy;
	 
	 }
	 public void setUploadedBy(String uploadedBy) {
	 this.uploadedBy = uploadedBy;
	 }
	 public String getUploadedDate() {
	 return uploadedDate;
	 }
	 public void setUploadedDate(String uploadedDate) {
	 this.uploadedDate = uploadedDate;
	 }
	 public String getUploadFileName() {
	 return uploadFileName;
	 }
	 public void setUploadFileName(String uploadFileName) {
	 this.uploadFileName = uploadFileName;
	 }*/
	public String getUpoadFileName() {
		return upoadFileName;
	}
	public void setUpoadFileName(String upoadFileName) {
		this.upoadFileName = upoadFileName;
	}
	
	public String getStartWeekDay(int index) {
		return ((AccessPolicyDetailData)lstWeeKDay.get(index)).getStartWeekDay();
	}
	
	public void setStartWeekDay(int index,String startWeekDay){
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setStartWeekDay(startWeekDay);
	}
	
	public Timestamp getStartTime(int index) {
		return ((AccessPolicyDetailData)lstWeeKDay.get(index)).getStartTime();
	}
	
	public void setStartTime(int index, Timestamp startTime){
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setStartTime(startTime);
	}
		
	public void setStartHours(int index, int startHours){
		
		GregorianCalendar gCal = new GregorianCalendar();
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		
		int minutes = getMinutes(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStartTime());
		if(minutes <= 0)minutes = 0;
		
		gCal.set(1700,1,1,startHours,minutes,0);
		java.sql.Timestamp startTime = new java.sql.Timestamp(gCal.getTime().getTime());
	
		
		
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
		int hours = getMinutes(((AccessPolicyDetailData)lstWeeKDay.get(index)).getStopTime()); 
		return hours;
	}
		
	/*public void setStopTime(int index,Timestamp stopTime) {
		System.out.println("Getting setStopTime(timestamp)");
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		
		//((AccessPolicyDetailData)lstParameterValue.get(index)).setStopTime(stopTime);
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setStopTime(stopTime);
	}*/
	/*public void setStrStopTime(int index, String strStopTime){
		System.out.println("Getting setStrStopTime(String)");
		GregorianCalendar gCal = new GregorianCalendar();
		while(lstWeeKDay.size() - 1 < index)
			lstWeeKDay.add(new AccessPolicyDetailData());
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		StringTokenizer tokenizer = new StringTokenizer(strStopTime,":");
		gCal.set(1700,1,1,Integer.parseInt(tokenizer.nextToken()),Integer.parseInt(tokenizer.nextToken()),0);
		//date = sdf.parse(strStopTime);
		java.sql.Timestamp stopTime = new java.sql.Timestamp(gCal.getTime().getTime());
		
		//java.sql.Timestamp stopTime = new java.sql.Timestamp(Long.parseLong(strStopTime));
		((AccessPolicyDetailData)lstWeeKDay.get(index)).setStopTime(stopTime);
	}*/
	
	/*public String getStrStopTime(int index) {
		System.out.println("Getting getStrStopTime(String)");
		String returnStr = "";
		Timestamp timestamp = ((AccessPolicyDetailData)lstWeeKDay.get(index)).getStopTime();		
		System.out.println("Getting getStrStopTime(String)");
		if(timestamp != null){
			//returnStr = timestamp.toString();
			int hour = timestamp.getHours();
			int mins = timestamp.getMinutes();
			returnStr = hour+":"+mins;
		}
		if(returnStr == null || returnStr.equalsIgnoreCase("")){
			returnStr = "0:0";
		}

		return returnStr;

	}*/
	
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
			
	public int getItemIndex() {
		return itemIndex;
	}
	
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public String getWeekDayId() {
		return weekDayId;
	}
	
	public void setWeekDayId(String weekDayId) {
		this.weekDayId = weekDayId;
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
	
}
