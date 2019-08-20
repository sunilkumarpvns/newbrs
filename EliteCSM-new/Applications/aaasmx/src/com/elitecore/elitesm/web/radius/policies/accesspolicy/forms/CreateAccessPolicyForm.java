package com.elitecore.elitesm.web.radius.policies.accesspolicy.forms;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyDetailData;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;


public class CreateAccessPolicyForm extends BaseDictionaryForm{
	
	private String name;
	private String description;
	private boolean allowed;
	private boolean denied;
	private boolean show;
	private FormFile fileUpload;
	private String uploadFileName;
	private String uploadedBy;
	private String uploadedDate;
    private String policyList;
	List lstParameterValue = new ArrayList();
	

	
	/*private String serialNumber;
	private String startWeekDay;
	private String endWeekDay;
	private Date startTime;
	private Date stopTime;
	private String remove;
	private String allowedDenied;*/
	
	
	
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
	
	
	public FormFile getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(FormFile fileUpload) {
		this.fileUpload = fileUpload;
	}
	public String getUploadedBy() {
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
	}
	
	
	
	
	/**
	 * @return Returns the alloweddenied.
	 */
	public String getAccessStatus(int index) {
		return ((AccessPolicyDetailData)lstParameterValue.get(index)).getAccessStatus();
	}
	/**
	 * @param alloweddenied The alloweddenied to set.
	 */
	public void setAccessStatus(int index,String accessStatus) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new AccessPolicyDetailData());
		
		((AccessPolicyDetailData)lstParameterValue.get(index)).setAccessStatus(accessStatus);
	}
	/**
	 * @return Returns the endday.
	 */
	public String getEndWeekDay(int index) {
		return ((AccessPolicyDetailData)lstParameterValue.get(index)).getEndWeekDay();
	}
	/**
	 * @param endday The endday to set.
	 */
	public void setEndWeekDay(int index,String endWeekDay) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new AccessPolicyDetailData());
		
		((AccessPolicyDetailData)lstParameterValue.get(index)).setEndWeekDay(endWeekDay);
	}
	/**
	 * @return Returns the endtime.
	 */
	public Date getStopTime(int index) {
		return ((AccessPolicyDetailData)lstParameterValue.get(index)).getStopTime();
	}
	/**
	 * @param endtime The endtime to set.
	 */
	public void setStopTime(int index,Timestamp stopTime) {
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new AccessPolicyDetailData());
		
		//((AccessPolicyDetailData)lstParameterValue.get(index)).setStopTime(stopTime);
		((AccessPolicyDetailData)lstParameterValue.get(index)).setStopTime(stopTime);
	}
	
	/**
	 * @return Returns the serialnumber.
	 */
	/*public int getSerialNumber(int index) {
		return ((AccessPolicyDetailData)lstParameterValue.get(index)).getSerialNumber();
	}*/
	/**
	 * @param serialnumber The serialnumber to set.
	 */
	/*public void setSerialNumber(int index,int serialNumber){
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new AccessPolicyDetailData());
		((AccessPolicyDetailData)lstParameterValue.get(index)).setSerialNumber(serialNumber);
	}*/
	/**
	 * @return Returns the startday.
	 */
	public String getStartWeekDay(int index) {
		return ((AccessPolicyDetailData)lstParameterValue.get(index)).getStartWeekDay();
	}
	/**
	 * @param startday The startday to set.
	 */
	public void setStartWeekDay(int index,String startWeekDay){
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new AccessPolicyDetailData());
		((AccessPolicyDetailData)lstParameterValue.get(index)).setStartWeekDay(startWeekDay);
	}
	/**
	 * @return Returns the starttime.
	 */
	public Date getStartTime(int index) {
		return ((AccessPolicyDetailData)lstParameterValue.get(index)).getStartTime();
	}
	/**
	 * @param starttime The starttime to set.
	 */
	public void setStartTime(int index, Timestamp startTime){
		while(lstParameterValue.size() - 1 < index)
			lstParameterValue.add(new AccessPolicyDetailData());
		((AccessPolicyDetailData)lstParameterValue.get(index)).setStartTime(startTime);
	}
	/**
	 * @return Returns the lstParameterValue.
	 */
	public List getLstParameterValue() {
		return lstParameterValue;
	}
	/**
	 * @param lstParameterValue The lstParameterValue to set.
	 */
	public void setLstParameterValue(List lstParameterValue) {
		this.lstParameterValue = lstParameterValue;
	}
	/**
	 * @return Returns the policyList.
	 */
	public String getPolicyList() {
		return policyList;
	}
	/**
	 * @param policyList The policyList to set.
	 */
	public void setPolicyList(String policyList) {
		this.policyList = policyList;
	}
}
